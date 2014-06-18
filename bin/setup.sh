#!/bin/sh

script_dir=`dirname $0`
root_dir=`cd "$script_dir"/..; echo $PWD`
lib_dir="$root_dir/lib"

mvn install:install-file -Dfile=$lib_dir/nysenate-java-utils-1.0.0.jar -DpomFile=$lib_dir/nysenate-java-utils-1.0.0.pom

