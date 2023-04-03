# Setting up file uploads

## Prerequisites
Please read `0001-approach-for-document-uploads.md` before following this guide.

## Functionality overview

The file upload functionality is split over 4 packages.
Each package has a separate area of concern and coupling between each is kept to a minimum

### Packages overview
* s3
* virus
* file
* supportingdocuments (product specific i.e. ReMI)

### S3 (s3 package)
* Provides a service for:
  * uploading an object to an S3 bucket
  * downloading an object from an S3 bucket
  * deleting an object from an S3 bucket
  * checking whether an S3 bucket exists
  * checking whether an object exists in an S3 bucket

### Virus scanning (virus package)
* Provides a service for scanning a file input stream for virus

### File upload (file package)
Is coupled to the s3 and virus packages, hences those packages are nested within file

* Provides a virtual folder enum to organise file uploads
* Provides a service for:
  * uploading a file to virtual folder and creating a record in the database
    * validating files meet the following configurable parameters:
      * Are of a certain file extension
      * Are within a certain file size limit
    * strips certain characters from the filename before storage (configurable regex)
  * downloading a file from a virtual folder
  * deleting a file from a virtual folder and deleting the associated database record
* Provides a number of utils including for validation of file upload descriptions
* Provides a number of necessary classes to integrate into FDS' file-upload macro

### Supporting Documents (supportingdocuments package)
_Product specific package that ties the files to the business use case._  

Is coupled to the file package.

* Needs to provide:
  * A service to link uploaded files to a file uploads form field
  * A controller to download, upload and delete files
    * Controller should also do any necessary product specific security checks
  * A service for:
    * Recording file upload records against a form field
    * Removing records made against a form field and associated file upload records

## How to set up in a new product

### Setup S3
1. Add the `aws-java-sdk` library to your products gradle build file. E.g:  
   `implementation 'com.amazonaws:aws-java-sdk-s3:1.12.307'`
2. Duplicate the `file.s3` package into your product (both in main and test)
3. Add a s3Mock instance to your local-dev docker container. E.g:
   ```
   s3mock:
     image: adobe/s3mock:2.1.28
     ports:
       - "9090:9090"
       - "9191:9191"
     environment:
       initialBuckets: remi-dev
       retainFilesOnExit: true
       root: ./s3data
   ```
4. Add the following properties to `application-development.properties`
   ```
   # S3
   s3.access-key=
   s3.secret-key=
   s3.bucket-name={product-acronym}-dev
   s3.endpoint=localhost:9090
   s3.region-name=
   s3.disable-ssl=true
   s3.proxy-host=
   s3.proxy-port=
   ```
5. Add the following properties to `application-production.properties`
   ```
   # S3
   s3.access-key=${S3_ACCESS_KEY}
   s3.secret-key=${S3_SECRET_KEY}
   s3.bucket-name=${S3_BUCKET_NAME}
   s3.endpoint=${S3_ENDPOINT:s3.eu-west-2.amazonaws.com}
   s3.region-name=${S3_REGION_NAME:eu-west-2}
   s3.disable-ssl=${S3_DISABLE_SSL:false}
   s3.proxy-host=${S3_PROXY_HOST:}
   s3.proxy-port=${S3_PROXY_PORT:}
   ```

### Setup Virus scanning
1. Add the `clamav-client` library to your products gradle build file. E.g:  
   `implementation 'fi.solita.clamav:clamav-client:1.0.1'`
2. Duplicate the `file.virus` package into your product (both in main and test)
3. Add a clamav instance to your local-dev docker container. E.g:
   ```
   clamav:
     image: clamav/clamav:0.105
     ports:
       - "3310:3310"
   ```
4. Add the following properties to `application-development.properties`
   ```
   # ClamAV
   clamav.host=localhost
   clamav.port=3310
   clamav.timeout=60000
   ```
5. Add the following properties to `application-production.properties`
   ```
   # ClamAV
   clamav.host=${CLAMAV_HOST}
   clamav.port=${CLAMAV_PORT}
   clamav.timeout=${CLAMAV_TIMEOUT}
   ```
### Setup File uploads
1. Duplicate the contents of the `file` package into your product's file package (both in main and test)
2. Add the following properties to `application.properties`
   ```
   # File upload
   # Replace '/', '\', '?', '%', '*', ':', '|', '"', '<', '>' from the provided fileName.
   file-upload.filename-disallowed-characters-regex=[/\\?%*:|\"<>]
    
   spring.servlet.multipart.max-file-size=${file-upload.max-allowed-size}
   spring.servlet.multipart.max-request-size=${file-upload.max-allowed-size}
   ```
3. Add the following properties to `application-development.properties`
   ```
   # File upload
   file-upload.max-allowed-size=52428800
   file-upload.allowed-extensions=.bmp, .doc, .docx, .jpeg, .jpg, .pdf, .png, .txt, .xls, .xlsx
   ```
   1. Add more file type extensions as needed
4. Add the following properties to `application-production.properties`
   ```
   # File upload
   file-upload.max-allowed-size=${FILE_UPLOAD_MAX_ALLOWED_SIZE}
   file-upload.allowed-extensions=${FILE_UPLOAD_ALLOWED_EXTENSIONS}
   ```
5. Create a flyway upgrade script to create a table for storing uploaded file records
   ```
   CREATE TABLE uploaded_files (
       id UUID PRIMARY KEY,
       s3_key TEXT NOT NULL,
       bucket_name TEXT NOT NULL,
       virtual_folder TEXT,
       filename TEXT,
       file_content_type TEXT,
       file_size_bytes NUMERIC,
       uploaded_time_stamp TIMESTAMP NOT NULL,
       description TEXT
   );
   ```
### Setup product specific layer (Supporting Documents, etc.)
1. Create a package for your product specific layer, e.g. `supportingdocuments`
2. Create the soft infrastructure to keep track of the multiples of uploaded files linked to a file uploads form field. See `submissiondetails.supportingdocuments` for an example
   1. Hibernate entity, see `SupportingDocument`
   2. A database table to store the links. Example flyway script:
   ```
   CREATE TABLE supporting_documents (
       id UUID PRIMARY KEY,
       submission_detail_id UUID NOT NULL,
       supporting_document_type TEXT NOT NULL,
       uploaded_file_id UUID NOT NULL,
       CONSTRAINT supporting_documents_fk1 FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id),
       CONSTRAINT supporting_documents_fk2 FOREIGN KEY (uploaded_file_id) REFERENCES uploaded_files (id)
   );
   
   CREATE INDEX supporting_documents_idx1 ON supporting_documents(submission_detail_id);
   CREATE INDEX supporting_documents_idx2 ON supporting_documents(uploaded_file_id);
   ``` 
   3. Repository, see `SupportingDocumentRepository`
   4. An enum type for all the file uploads form fields instances, see `SupportingDocumentType`
3. Create a controller to coordinate uploads, downloads, deletions, see `SubmissionSupportingDocumentsController`
4. Create a service to manage the uploads, downloads, deletions of files, and manage the database records. See `SupportingDocumentService`
5. See `0002-how-to-add-supporting-docs-file-upload.md` for a guide on how to implement a file upload/supporting document instance