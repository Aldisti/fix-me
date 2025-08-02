#!/bin/bash

main() {
	local file="$1"
	if [ "$file" = "" ]; then
		local file=".env"
	fi

	local pwd="$(python3 -c "import secrets; print(secrets.token_urlsafe().replace('-', '').replace('_', ''))")"
	echo "\
DB_HOST=localhost:27017
DB_USERNAME=java
DB_PASSWORD=$pwd
DB_NAME=market
DB_COLLECTION=transactions
" > "$file"
}

main $@

