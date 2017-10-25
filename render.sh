#!/bin/bash
(midi2abc $1/generated/$2.mid >> $1/generated/$2.abc && abcm2ps $1/generated/$2.abc -O $1/generated/$2.ps -q || true  && ps2pdf $1/generated/$2.ps $1/generated/$2.pdf || true) &>/dev/null
