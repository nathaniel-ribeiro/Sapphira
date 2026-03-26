# Sapphira - Anti-Cheat for Online Xiangqi

## Overview
This project is a webservice designed to flag anomalous Xiangqi player behavior. 

### Anomaly Detection by Isolation Forest
Due to the high volume of online games played per day, a screening model is used to avoid wasting compute on analyzing the games of non-suspicious players.
We use an Isolation Forest (iForest) to detect unusual (but not necessarily cheating) behaviors. An major advantage of iForests over statistical anomaly detection is that iForests
tolerate contamination of the training data and do not require the proportion of contaminated values to be known a priori. The intuition of iForest is that if we recursively split our dataset with random hyperplanes, it would require
fewer splits to isolate the outliers. At inference time, the forest detects anomalous games which can then be selected for deeper analysis.

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
Sapphira and all its dependencies are written in pure Java and Kotlin, meaning it can be run on any platform with a JRE supporting Java 25+ 
(Windows, Mac, Linux, and many more!). Building from source requires the full JDK, but you can run a fat JAR with just the JRE.
### Compatible with top-rated Xiangqi engine Pikafish.
Since Sapphira is NOT a Xiangqi engine, it offloads the task of evaluating positions and understanding the game's logic 
to tried and tested external software. (You'll need to download [Pikafish](https://github.com/official-pikafish/Pikafish).) 
Sapphira was originally intended to be compliant with *any* UCI-compliant engine, but currently relies on Pikafish-specific commands 
that are not part of the UCI protocol. Future work may change this, but as of writing, Pikafish is the strongest and fastest engine, so
there is really no reason to use anything else.
### Built to scale.
Sapphira can easily vertically and horizontally scale. The command line interface allows the user to run many concurrent, multi-threaded instances of Pikafish, allowing
full use of the hardware available. Horizontal scaling is also possible as the API is RESTful, but you'll have to implement the routing and load-balancing yourself.

## Terms of Use
Sapphira is free and distributed under the GNU General Public License version 3 (GPL v3). Essentially, this means you are free to do almost exactly what you want with the program, including distributing it among your friends, making it available for download from your website, selling it (either by itself or as part of some bigger software package), or using it as the starting point for a software project of your own. The only real limitation is that whenever you distribute Sapphira in some way, you MUST always include the license and the full source code (or a pointer to where the source code can be found) to generate the exact binary you are distributing. If you make any changes to the source code, these changes must also be made available under GPL v3.

## Acknowledgements
This work would not have been possible without the advice and support of [@DavidK](https://play.xiangqi.com/@DavidK) and Dr. Ken Regan!
