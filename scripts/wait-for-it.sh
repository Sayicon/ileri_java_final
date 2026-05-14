#!/bin/bash
# wait-for-it.sh — HOST:PORT hazir olana kadar bekler
# Kullanim: wait-for-it.sh host:port [-t timeout] [-- command]

WAITFORIT_cmdname=${0##*/}
WAITFORIT_timeout=15
WAITFORIT_quiet=0
WAITFORIT_host=""
WAITFORIT_port=""

usage() {
  cat << USAGE
Kullanim: $WAITFORIT_cmdname host:port [-t timeout] [-- komut]
  -t TIMEOUT  Bekleme suresi (saniye, varsayilan: 15)
  -q          Sessiz mod
  -- KOMUT    Hazir olunca calistirilacak komut
USAGE
  exit 1
}

wait_for() {
  local start_ts
  start_ts=$(date +%s)
  while :; do
    if (echo > /dev/tcp/"$WAITFORIT_host"/"$WAITFORIT_port") 2>/dev/null; then
      local end_ts
      end_ts=$(date +%s)
      [ "$WAITFORIT_quiet" -eq 0 ] && echo "$WAITFORIT_cmdname: $WAITFORIT_host:$WAITFORIT_port hazir — $((end_ts - start_ts))s bekledik"
      return 0
    fi
    local now
    now=$(date +%s)
    if [ $((now - start_ts)) -ge "$WAITFORIT_timeout" ]; then
      echo "$WAITFORIT_cmdname: TIMEOUT — $WAITFORIT_host:$WAITFORIT_port $WAITFORIT_timeout saniyede hazir olmadi"
      return 1
    fi
    sleep 1
  done
}

# Argüman ayrıştırma
if [ "$#" -lt 1 ]; then usage; fi

hostport="$1"; shift
WAITFORIT_host="${hostport%%:*}"
WAITFORIT_port="${hostport##*:}"

while [ "$#" -gt 0 ]; do
  case "$1" in
    -t) WAITFORIT_timeout="$2"; shift 2 ;;
    -q) WAITFORIT_quiet=1; shift ;;
    --) shift; break ;;
    *) usage ;;
  esac
done

if [ -z "$WAITFORIT_host" ] || [ -z "$WAITFORIT_port" ]; then usage; fi

wait_for
RESULT=$?

if [ "$#" -gt 0 ] && [ "$RESULT" -eq 0 ]; then
  exec "$@"
fi

exit "$RESULT"
