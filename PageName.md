# Introduction #

Though simple, this project is ready to use


## Install ##
Grab the equinox-headless-service.tar.gz file from the Downloads page.
Either extract in the root directory or open it up and do the following
  * copy the `equinox_bootstrap` file to `/etc/init.d`
  * symlink to the appropriate runlevel directory as desired
  * create a `equinox_bootstrap` directory in `/etc/`
  * copy the `bootstrap.conf` file into it
  * create an `equinox/bin` directory under `/var/`
  * copy the two jar files into it

Be sure to edit **`/etc/equinox_bootstrap/bootstrap.conf`** to set the user name to run Equinox as and the `JAVA_HOME`.
You can set the admin port as well.

to start the service:

`/etc/init.d/equinox_bootstrap start`

to check the status

`/etc/init.d/equinox_bootstrap status`

to stop it

`/etc/init.d/equinox_bootstrap stop`

Problems? please file an issue