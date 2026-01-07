# Sapphira - Anti-Cheat for Online Xiangqi

## Overview
This project brings the seminal work of Dr. Ken Regan on anti-cheat for chess to the game of Xiangqi. Note that this
repository is a work-in-progress.

### Regan's IPR Method
Unlike ELO, which uses only the *outcomes* of a game to determine a player's approximate strength, Regan's IPR (Intrinsic
Performance Rating) uses the quality of individual moves. Through two parameters, sensitivity and consistency, 
we can predict the probability that a player will choose an inferior move. Sensitivity models the player's ability to discern between two roughly equal (but subtly different) moves and 
consistency models the player's ability to avoid bad moves. 

A player whose IPR for a game or set of games significantly exceeds their recorded ELO can be branded a cheater. 
You can read a more thorough explanation [here](https://cse.buffalo.edu/~regan/papers/pdf/Reg12IPRs.pdf).

### Where does the name come from?
> But a certain man named Ananias, with Sapphira his wife, sold a possession,
> And kept back part of the price, his wife also being privy to it, and brought a certain part, and laid it at the apostles' feet.
> But Peter said, Ananias, why hath Satan filled thine heart to lie to the Holy Ghost, and to keep back part of the price of the land?
> Whiles it remained, was it not thine own? and after it was sold, was it not in thine own power? why hast thou conceived this thing in thine heart? thou hast not lied unto men, but unto God.
> And Ananias hearing these words fell down, and gave up the ghost: and great fear came on all them that heard these things.
> And the young men arose, wound him up, and carried him out, and buried him.
> And it was about the space of three hours after, when his wife, not knowing what was done, came in.
> And Peter answered unto her, Tell me whether ye sold the land for so much? And she said, Yea, for so much.
> Then Peter said unto her, How is it that ye have agreed together to tempt the Spirit of the Lord? behold, the feet of them which have buried thy husband are at the door, and shall carry thee out.
> Then fell she down straightway at his feet, and yielded up the ghost: and the young men came in, and found her dead, and, carrying her forth, buried her by her husband.
> And great fear came upon all the church, and upon as many as heard these things.

This project seeks to strike down duplicitous players just as Ananias and Sapphira were struck down for their deceit!

## Features
### Detects elevator/alt accounts.
Online Xiangqi differs from OTB Xiangqi in that players can play *themselves* with throw-away/alt/elevator accounts in order to artificially boost their ELO. 
Sapphira detects these accounts using text similarity metrics, which should be sufficient to catch most such accounts.
### Platform-agnostic. 
Sapphira and all its dependencies are written in pure Java and Kotlin, meaning it can be run on any platform with a JRE supporting Java 17+ 
(Windows, Mac, Linux, and many more!). Building from source requires the full JDK, but you can run a fat JAR with just the JRE.
### Compatible with top-rated Xiangqi engine Pikafish.
Since Sapphira is NOT a Xiangqi engine, it offloads the task of evaluating positions and understanding the game's logic 
to tried and tested external software. (You'll need to download [Pikafish](https://github.com/official-pikafish/Pikafish).) 
Sapphira was originally intended to be compliant with *any* UCI-compliant engine, but currently relies on Pikafish-specific commands 
that are not part of the UCI protocol. Future work may change this, but as of writing, Pikafish is the strongest and fastest engine, so
there is really no reason to use anything else.
### Always open-source, always free.
Licensed under GPLv3, you are free to copy, modify, distribute, and even sell Sapphira so long as you also release your 
modifications under GPLv3 or provide a pointer to the source code. While not required by the license agreement, 
I would appreciate a shout-out if you do use this project!
