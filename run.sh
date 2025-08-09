#!/bin/bash


RESET="\033[0m"
GREEN="\033[32;1m"
RED="\033[31;1m"
CYAN="\033[36;1m"

VERSION="1.1.0"

DEBUG="false"
QUIET="false"

ROOT_DIR="$PWD/$(dirname $0)"

main() {
	while [ $# -gt 0 ]; do
		opt="$(echo $1 | tr 'A-Z' 'a-z')"
		shift
		case ${opt} in
			broker)
				__broker
				return $?
			;;
			market)
				__market $@
				return $?
			;;
			router)
				__router
				return $?
			;;
			mongo|database)
				local cmd="$1"
				if [ "$cmd" = "" ]; then
					local cmd="up"
				else
					shift
				fi
				__database $cmd
				return $?
			;;
			-q | --quiet)
				QUIET="true"
			;;
			-d | --debug)
				DEBUG="true"
			;;
			*)
				__error "Invalid argument '$opt'"
				exit 1
			;;
		esac
	done
}

# 1: type <broker|market|router>
__execute() {
	local project_dir="$ROOT_DIR/fix-me"
	local exe="$project_dir/$1/target/$1-$VERSION-jar-with-dependencies.jar"
	if ! [ -f $exe ]; then
		__debug "Compiling code..."
		cd $project_dir
		mvn -q clean package
		cd ..
	fi
	shift # removes the 'market' params so that '$@' expands to only the required params
	__debug "Starting application with params: $@"
	java -jar $exe $@
}

__broker() {
	__execute "broker"
}

__market() {
	__check_env
	export $(cat $ROOT_DIR/.env | tr '\n' ' ')
	__execute "market" $@
}

__router() {
	__execute "router"
}


__check_env() {
	local env_file="$ROOT_DIR/.env"
	if [ ! -f "$env_file" ]; then
		$ROOT_DIR/mongo/gen_env.sh "$env_file"
	fi
}

__database() {
	local env_file="$ROOT_DIR/.env"
	local compose="$ROOT_DIR/mongo/docker-compose.yml"

	case "$1" in
		up)
			__check_env
			docker compose -f "$compose" --env-file "$env_file" up -d
		;;
		down)
			docker compose -f "$compose" down
		;;
		clean)
			docker rm -f fix-me-mongo
			docker volume rm fix-me-mongo
			rm "$env_file"
		;;
		*)
			__error "Invalid command '$1' for mongoDB"
		;;
	esac
}


# 1: type
# 2: color
# 3: message
__log() {
	echo -e "$2$1$RESET: $3"
}

# 1: message
__debug() {
	if [ "$DEBUG" = "true" ]; then
		__log "DEBUG" $CYAN "$1"
	fi
}

# 1: message
__info() {
	if [ "$QUIET" != "true" ]; then
		__log "INFO" $GREEN "$1"
	fi
}

# 1: message
__error() {
	__log "ERROR" $RED "$1"
}

set -e

main $@

