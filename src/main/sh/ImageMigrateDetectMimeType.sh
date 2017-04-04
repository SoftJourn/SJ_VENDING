#!/usr/bin/env bash
for fileName in $HOME/Pictures/db_images/mysql-files *; do
IFS='_' read -ra ADDR <<< "$fileName"
mkdir ${ADDR[1]}
contentType=$(xdg-mime query filetype $fileName)
extension=${contentType#image/}
newName="${ADDR[0]}.$extension"
mv $fileName "${ADDR[1]}/$newName"
done