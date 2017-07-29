# FLAK data converter

The data converter is being used to convert existing and compressed JSON files
to simple and flat CSV formatted files.

In our case CSV files are better because

* they have less syntactic overhead
* can be processed faster because data just needs to be splitted by a separator
* Big Data technologies have built-in support for CSV formatted data

One CSV file that is being generated includes data samples from 100 raw trips.

The trips.csv file need to include all trips recorded. This is important to get
the timestamp for the start of the trip. (see conversion steps)


## Conversion steps

* use original gzipped JSON data
* decompress
* normalize acceleration data
* create absolute timestamp from elapsed time
* create and write CSV row
