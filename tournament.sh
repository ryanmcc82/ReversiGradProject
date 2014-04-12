#!/bin/bash
set -x

group_strategies=$(find src/main/java -name "Group*Strategy.java" | \
                   sed -e 's/\//./g' -e 's/src.main.java.//g' -e 's/.java//g' | \
                   tr '\n' ' ')
mvn exec:java \
-Dexec.mainClass="edu.uab.cis.reversi.Reversi" \
-Dexec.args="--games 10 --timeout 1000 --strategies \
edu.uab.cis.reversi.strategy.baseline.RandomStrategy \
edu.uab.cis.reversi.strategy.baseline.PositionalStrategy \
edu.uab.cis.reversi.strategy.baseline.PreferCornersStrategy \
$group_strategies"
