#!/bin/bash


RESET="\033[0m"
GREEN="\033[32;1m"
RED="\033[31;1m"
CYAN="\033[36;1m"

DEBUG="false"
QUIET="false"

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
				__market
				return $?
			;;
			router)
				__router
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
	local project_dir="$PWD/$(dirname $0)/fix-me"
	local exe="$project_dir/$1/target/$1-1.0.0-jar-with-dependencies.jar"
	if ! [ -f $exe ]; then
		__debug "Compiling code..."
		cd $project_dir
		mvn -q clean package
		cd ..
	fi

	__debug "Starting application..."
	java -jar $exe
}

__broker() {
	__execute "broker"
}

__market() {
	__execute "market"
}

__router() {
	__execute "router"
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

