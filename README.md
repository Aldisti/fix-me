
# Description

This project is composed of 4 components:
    - **Market**
    - **Broker**
    - **Router**
    - **Database** (MongoDB)

The following is a simple representation of how the components
communicate with each other.

```
 +--------+         +--------+         +--------+
 | Broker | <-----> | Router | <-----> | Market | ----> [MongoDB]
 +--------+         +--------+         +--------+
```

# Installation

1. Before anything else, setup the database following this [guide][mongo/README.md]

2. To start up the other components, you can use the marvelous utility `run.sh`.

3. Start the router, it needs to be the first one.

```bash
./run.sh router
```

4. Then you can start the broker and market.

```bash
./run.sh market
```

```bash
./run.sh broker
```

