#!/bin/bash

main() {
	local file="$1"
	if [ "$file" = "" ]; then
		local file=".env"
	fi

	local pwd1="$(python3 -c "import secrets; print(secrets.token_urlsafe().replace('-', '').replace('_', ''))")"
	local pwd2="$(python3 -c "import secrets; print(secrets.token_urlsafe().replace('-', '').replace('_', ''))")"
	echo "\
DB_ADMIN=admin
DB_ADMIN_PASSWORD=$pwd1
DB_HOST=localhost:27017
DB_USERNAME=java
DB_PASSWORD=$pwd2
DB_NAME=market
DB_COLLECTION=transactions
" > "$file"
}

main $@

