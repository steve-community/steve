### How can I upgrade from 1.x.x to 2.x.x? ###

The major release 2.0.0 is backwards incompatible with existing installations,
since the migration scripts are manually altered ([See commit](https://github.com/RWTH-i5-IDSG/steve/commit/3eb3a9dbc9285a7a6e3316873c8dd892f96b90f8)) and the checksums in the metadata
table of Flyway (our DB migration tool) need to be corrected.
This can be done executing the following command:

    mvn initialize flyway:repair
