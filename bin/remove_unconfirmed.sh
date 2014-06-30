#!/bin/sh
#
# Clean up user records that have been unconfirmed for more than one week.
#
# Project: BillBuzz
# Author: Ken Zalewski
# Organization: New York State Senate
# Date: 2014-06-30
# 

sql="delete from billbuzz_user where activated=0 and confirmedat is null and createdat < date_sub(now(), interval 7 day);"

echo "$sql" | mysql billbuzz -u billbuzzadmin -p

