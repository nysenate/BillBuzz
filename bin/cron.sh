#!/bin/bash
source $(dirname "$0")/utils.sh

# This should run once a day at the time we'd like digests to go out.
$ROOTDIR/bin/run.sh UpdateSenators -e $ROOTDIR/target/BillBuzz-$VERSION/WEB-INF/classes/app.properties
$ROOTDIR/bin/run.sh UpdatePosts -e $ROOTDIR/target/BillBuzz-$VERSION/WEB-INF/classes/app.properties
$ROOTDIR/bin/run.sh SendDigests -e $ROOTDIR/target/BillBuzz-$VERSION/WEB-INF/classes/app.properties

