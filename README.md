# SwaggySearch

## Project Goals

SwaggySearch is a simple, intuitive, and easy-to-use Wikipedia search engine. Our goals for SwaggySearch was to improve user experience, which we accomplished by implementing a GUI for the user to input search terms and see results. We also wanted greater search accuracy by accounting for typos. Finally, we improved search logic by implementing TF-IDF and changing the way AND searches calculate relevance.


## Usage

After cloning the repository, navigate into the repository and run:

```
ant build
```
This will compile all the files necessary. To launch the search engine, simply run:

```
ant SwaggySearch
```
This will bring up a separate window. Enter your search term(s) in the text field on top, and your top ten search results will appear in the text area below the search field, organized by decreasing relevance.

## Search Examples

To search for "apples", you simply type in
```
apples
```
and press enter.


To display the pages containing both the terms "apples" *and* "oranges", type in
```
apples AND oranges
```
and press enter.


To display the pages containing either "apple" *or* "oranges", type in
```
apples OR oranges
```
and press enter.


To display the pages containing the term "apple" but *not* the term "oranges", type in
```
apples NOT oranges
```
and press enter.

## Features
These are the features we added to improve SwaggySearch:
* GUI - a simple, intuitive GUI made using JFrame that allows user to input a search term, and displays the top ten search results with hyperlinks.
* Fuzzy Search - a very simple implementation that maximises the probability of searching for a word in a pre-defined dictionary. It uses deletion, transposition, replacement and insertion to get the highest probability. Note: Currently, this feature only works for single search words.
* Search logic - implemented TF-IDF to prioritize results, as well as averaging relevance instead of adding for AND searches to account for worst-case searches.
