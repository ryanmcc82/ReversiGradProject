# Project: Reversi

The goal of this class project is to give you some experience designing strategies for a competitive game. It assumes you have already familiarized yourself with Git and Maven in a previous assignment.

## Set up a repository for your group

This is a group assignment, so your group will need to turn in a single solution to the problem. As a result, you will need to decide between the members of your group who will host the primary repository. All other members of the group will push and pull changes to and from this primary repository.

1. Have one member from your group fork the repository https://git.cis.uab.edu/cs-460/reversi.

2. Give your instructor access to this primary fork. **I cannot grade your assignment unless you complete this step.**

3. Give the other members of your group access to the primary fork.

Note that I will grade only a single fork per group, so be sure all members of your group are clear on where they should be pushing their code.

## Implement your part of the code

Your group's task is to design a strategy for playing [Reversi](http://en.wikipedia.org/wiki/Reversi). The [Wikipedia article](http://en.wikipedia.org/wiki/Reversi) gives a clear description of the rules, and you can use it as your reference. You may also find it useful to try playing a few times in [a version of the game online](http://othellogame.net/revello/).

You will be creating a new implementation of `edu.uab.cis.reversi.Strategy`. Your class should be named `edu.uab.cis.reversi.strategy.group<n>.Group<n>Strategy` and should be placed in a corresponding subfolder under the `src/main/java` directory. The group number, `<n>` will be assigned by your instructor.

Your `Strategy` should inspect the `edu.uab.cis.reversi.Board` that it is given, and decide upon a `Square` where you want to place a piece. Take a look at the documentation for the `Strategy` class, as well as the API of the `Board` class, to get an idea of what kinds of information you have access to.

## Test your code

There are no tests provided that can directly grade your code as correct or incorrect. Instead, you should compare your strategy to some baseline strategies. One example baseline strategy, `edu.uab.cis.reversi.strategy.baseline.RandomStrategy` is provided for you.

To test whether your strategy outperforms `RandomStrategy`, you can run `edu.uab.cis.reversi.Reversi`. See that class for the full set of options, but if you run something like:

    java edu.uab.cis.reversi.Reversi --strategies \
    edu.uab.cis.reversi.strategy.baseline.RandomStrategy \
    edu.uab.cis.reversi.strategy.group<n>.Group<n>Strategy

Then the class will have the strategies play several games against each other, and then print out the number of wins for each strategy. You can run the class with more than 2 strategies to see how your strategy would do in a round-robin style tournament.

## Submit your assignment

1.  To submit your group's assignment, make sure that your group has pushed all of your changes to your group's repository at `git.cis.uab.edu`.

2.  I will inspect the date of your last push to your `git.cis.uab.edu` repository. If it is after the deadline, your submission will be marked as late. So please **do not push changes to `git.cis.uab.edu` after the assignment deadline** unless you intend to submit a late assignment.
