Zip All Folders PowerShell Script
=================================

This script automatically zips each directory in the current location into a separate zip file, placing the contents of each directory at the root of the corresponding zip file.

How to Use
----------

1.  Make sure the PowerShell script `zip all folders.ps1` and the batch file are located in the directory you want to compress.

2.  Double-click the batch file to run the script. It will create a zip file for each directory in the current location.

Example
-------

Assuming you have the following directories:

```
/current_directory
  /dir1
    - file1.txt
  /dir2
    - file2.txt
```

After running the script, you will have:

```
/current_directory
  /dir1
    - file1.txt
  /dir2
    - file2.txt
  dir1.zip
  dir2.zip
```

And `dir1.zip` will contain `file1.txt` at the root level, `dir2.zip` will contain `file2.txt` at the root level.

* * * * *

Remember, double-click the batch file to run the script. No need for PowerShell or administrative access.