#!/bin/sh

prog=`basename $0`
script_dir=`dirname $0`
root_dir=`cd "$script_dir"/..; echo $PWD`
tomcat_home=/usr/share/tomcat
pom_file="$root_dir/pom.xml"

if [ ! -r "$pom_file" ]; then
  echo "$prog: $pom_file: Maven POM file not found" >&2
  exit 1
fi

app_ver=`php -r '$x=simplexml_load_file($argv[1]); echo $x->version;' $pom_file`
base_dir="$root_dir/target/billbuzz##$app_ver/WEB-INF"

SCRIPT=$1
shift

if [ ! "$SCRIPT" ]; then
  echo "$prog: Script name is a required argument." >&2
  exit 1
fi

java -Xmx1024m -Xms16m -cp "$base_dir/classes/:$base_dir/lib/*:$tomcat_home/lib/*" gov.nysenate.billbuzz.scripts.$SCRIPT -e $base_dir/classes/app.properties $@

