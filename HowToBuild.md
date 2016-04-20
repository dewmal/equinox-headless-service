# Get the source #
Follow the instructions on the [source page](http://code.google.com/p/equinox-headless-service/source)
```
svn checkout http://equinox-headless-service.googlecode.com/svn/trunk/ equinox-headless-service-read-only
```
Go into the checked-out directory
```
cd equinox-headless-service-read-only
```
Build it
```
mvn package
```

Find the jar in the target directory.

To work in Eclipse, after the steps above, run:
```
mvn -Declipse.workspace=./ eclipse:eclipse eclipse:add-maven-repo
```