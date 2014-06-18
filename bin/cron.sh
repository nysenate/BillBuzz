#!/bin/sh

script_dir=`dirname $0`

# This should run once a day at the time we'd like digests to go out.

$script_dir/run.sh UpdateSenators
$script_dir/run.sh UpdatePosts
$script_dir/run.sh SendDigests

