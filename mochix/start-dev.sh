#!/bin/sh
exec erl \
    -pa ebin deps/*/ebin \
    -pa ebin \
    -boot start_sasl \
    -sname mochix_dev \
    -s mochix \
    -s reloader
