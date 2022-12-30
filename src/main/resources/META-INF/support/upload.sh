#!/bin/sh

address='OLHIKHQLLPX9WYIRWGQKYPMUANJHXP9EVHDVXKDFPWKWYEXFOFKBGECVAWYOJRSQZYFQXFQHTTPPNRXUX'

file=$1

if [ "$file" = "" ]; then
	file="/etc/services"
fi

auth="user101:Nl2el1oLOg-cIbjZhWjSaBTUA0HuHU3d"


curl -i -u ${auth} -X POST -H "Content-Type: multipart/form-data" \
	-F "data=@${file}" \
	-F "address=${address}" http://localhost:3000/bcfs/store
