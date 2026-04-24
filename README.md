# Sapphira - Anti-Cheat for Online Xiangqi

## Overview
This project is a webservice designed to flag anomalous Xiangqi player behavior. 

### Anomaly Detection by Isolation Forest
Due to the high volume of online games played per day, a screening model is used to avoid wasting compute on analyzing the games of non-suspicious players.
We use an Isolation Forest (iForest) to detect unusual (but not necessarily cheating) behaviors. An major advantage of iForests over statistical anomaly detection is that iForests
tolerate contamination of the training data and do not require the proportion of contaminated values to be known a priori. The intuition of iForest is that if we recursively split our dataset with random hyperplanes, it would require
fewer splits to isolate the outliers. At inference time, the forest detects anomalous games which can then be selected for deeper analysis.

### Where does the name come from?
> Now a man named Ananias, together with his wife Sapphira, also sold a piece of property. With his wife’s full knowledge he kept back part of the money for himself, but brought the rest and put it at the apostles’ feet.
> Then Peter said, “Ananias, how is it that Satan has so filled your heart that you have lied to the Holy Spirit and have kept for yourself some of the money you received for the land? Didn’t it belong to you before it was sold? And after it was sold, wasn’t the money at your disposal? What made you think of doing such a thing? You have not lied just to human beings but to God.”
> When Ananias heard this, he fell down and died. And great fear seized all who heard what had happened. Then some young men came forward, wrapped up his body, and carried him out and buried him.
> About three hours later his wife came in, not knowing what had happened. Peter asked her, “Tell me, is this the price you and Ananias got for the land?”
>“Yes,” she said, “that is the price.”
> Peter said to her, “How could you conspire to test the Spirit of the Lord? Listen! The feet of the men who buried your husband are at the door, and they will carry you out also.”
> At that moment she fell down at his feet and died. Then the young men came in and, finding her dead, carried her out and buried her beside her husband. 11 Great fear seized the whole church and all who heard about these events.

This project seeks to strike down duplicitous players just as Ananias and Sapphira were struck down for their deceit!

## Features
### Platform-agnostic. 
Sapphira and all its dependencies are written in pure Java and Kotlin, meaning it can be run on any platform with a JRE supporting Java 25+ 
(Windows, Mac, Linux, and many more!). Building from source requires the full JDK, but you can run a fat JAR with just the JRE.
### Compatible with top-rated Xiangqi engine Pikafish.
Since Sapphira is NOT a Xiangqi engine, it offloads the task of evaluating positions and understanding the game's logic 
to tried and tested external software. (i.e. you'll need to [download Pikafish](https://drive.google.com/drive/folders/16-1o_vKzpcGMG3cy5uSfmEfhPght9tMV?usp=drive_link))
Sapphira was originally intended to be compliant with *any* UCI-compliant engine, but currently relies on Pikafish-specific commands 
that are not part of the UCI protocol. Future work may change this, but as of writing, Pikafish is the strongest and fastest engine, so there is really no reason to use anything else.

**NOTE:** Use the link in my repo to download Pikafish. If you compile the latest source code from the Pikafish GitHub yourself, you will end up with a binary that does not support a command that Sapphira relies on (setting ScoreType to PawnValueNormalized). The distribution-ready version of Pikafish (linked through Google Drive above) *does* support this key command. You can also download the latest version from the [official Pikafish website](https://www.pikafish.com) but as of writing the download links on that website are broken.

### Built to scale.
Sapphira can easily vertically and horizontally scale. The command line interface allows the user to run many concurrent, multi-threaded instances of Pikafish, allowing
full use of the hardware available. Horizontal scaling is also possible as the API is RESTful, but you'll have to implement the routing and load-balancing yourself.

## Terms of Use
Sapphira is free and distributed under the GNU General Public License version 3 (GPL v3). Essentially, this means you are free to do almost exactly what you want with the program, including distributing it among your friends, making it available for download from your website, selling it (either by itself or as part of some bigger software package), or using it as the starting point for a software project of your own. The only real limitation is that whenever you distribute Sapphira in some way, you MUST always include the license and the full source code (or a pointer to where the source code can be found) to generate the exact binary you are distributing. If you make any changes to the source code, these changes must also be made available under GPL v3.

## Acknowledgements
This work would not have been possible without the advice and support of [@DavidK](https://play.xiangqi.com/@DavidK) and [@inlandtaipan](https://play.xiangqi.com/@inlandtaipan)
