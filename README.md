# BlackJack
This is my project for analyzing BlackJack game data.
The project consists of the follows:
<li>BlackJackSorter.java - a code for sorting the input data. The data will be sorted by game session ID and and timestamp of the session. Then it will be written back into the input file</li>
<li>GameDataAnalyzer.java - tool for analyzing BlackJack and finding the errors. The first error of each game session found, will be written into a file called analyzer_results.txt</li>
<li>game_data_0/1/2.txt - data of the game</li>
<li>analyzer_results.txt - list of the first found errors for each game session</li>
<br></br>
For the project to work, follow these steps:
<li>Download the files</li>
<li>Run BlackJackSorter to sort the chosen game data file (you can change which file you want to sort inside the code - input file)</li>
<li>Run GameDataAnalyzer to analyze the sorted data (make sure that the input file for GameDataAnalyzer and BlackJackSorter are the same) for flaws and to write them into a file called analyzer_results.txt</li>
