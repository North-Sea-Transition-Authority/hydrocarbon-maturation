# Approach for document uploads

## Requirements

We require the ability to upload supporting documents of various types to AWS S3 for storage.

We also require that all supporting documents be scanned for viruses before they are uploaded to AWS S3. 

## Concepts and Conventions

### AWS S3

Amazon S3 or Amazon Simple Storage Service is a service offered by Amazon Web Services that provides object storage through a web service interface.

When storing files on a S3 service, you store files similar to how you would in a document store. In a document store such as NoSQL, you have a key value pair. Similarly, in S3 you have a key file pair.

On a S3 service, files are stored in buckets, within which you have a flat organisation structure. In other words there are no folders.

#### Buckets

Buckets are not analogous to folders, but are more analogous to drive partitions.

There are disadvantages to using buckets as folders:

1. You can't have nested buckets, so you would not be able to replicate folder hierarchy using buckets.
2. There are usually limits imposed by S3 service providers on how many buckets you can create before you would need to pay more fees

The primary benefits of using separate buckets are that you can assign separate policies to each:

* Reduced redundancy to save on costs.
* Versioning of changed contents
* Automatic archival to back-up services
* Separate permissions

For these reasons its common for people to use buckets for different file types (reports bucket, video bucket, etc.) or per project (project1 bucket, project2 bucket, etc.) or per environment (dev, pre-prod, production buckets) or a mix of all these approaches.

#### Folders

As S3 has a flat organisation structure and no physical folders, to provide some form of organisation, it has become convention to put a virtual path within the key itself thereby creating virtual folders. For example:

> **Key:**  
> `folder/subfolder/nested-subfolder/unique-filename`
 
Most S3 browsers honour this convention and will display the virtual folders as folders and nest the files within. 

#### Keys

One thing to keep in mind when creating a key for a file is that it must be unique. This seems obvious but when talking about keys, but if you use the files name as they key you may end up with duplicate candidate keys.

Therefore, its convention to either use a random value as a prefix to the filename in the key or a random value altogether. For the latter, this would mean you'd need to maintain an index mapping keys to filenames so that you could identify the file you have stored by filename.


## Implementation

### Buckets
We will use one bucket across our project for all uploaded files. There will however be one bucket per environment (production, pre-production, development, etc.)

### Keys
We will use a generated UUID for the key.  

### Virtual folders
We will make use virtual folders to organise our uploads, and we will make use of a Java Enum to provide a store of available folders.
Initially we will only have one folder, supporting-documents.   

### Virus Scanning
We will use ClamavClient from xyz.capybara to scan our uploads before uploading them to AWS S3.
We will scan uploaded files via Java InputStreams, thereby bypassing the need to copy uploaded files out of the temporary storage Spring Boot keeps Multi Part Files.
Any uploaded files found to contain a virus will not be uploaded. 

### Persistence
We will store a reference of all uploaded file in a database table. We will store the following information  per uploaded file:
* S3 Key
* Virtual Folder
* File name
* File content type
* File size bytes
* Uploaded timestamp
* File description