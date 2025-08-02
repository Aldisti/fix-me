
# Installation

1. First of all, run the MongoDB container with the following command:

```bash
./run.sh mongo up
```

2. Connect to the DB using the credentials inside the env file.
The vars to use are `DB_ADMIN` and `DB_ADMIN_PASSWORD`.

```bash
mongosh -u admin -p '<secret>'
```

3. Create the database and select it, the name of the db will be 'market'.

```mongosh
use market
```

4. At this point, we're ready to create the user.
As before, the name and password of the user are inside the env file.
The vars to use are `DB_USERNAME` and `DB_PASSWORD`.

```js
db.createUser({
    user: "java",
    pwd: "<>",
    roles: [{role: "readWrite", db: "market"}]
})
```

