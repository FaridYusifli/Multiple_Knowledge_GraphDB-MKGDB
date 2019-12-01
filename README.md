# Multiple Knowledge GraphDB (MKGDB)

## Abstract
We present MKGDB, a large-scale graph database created as a combination of multiple taxonomy backbones extracted from 5 existing knowledge graphs, namely:  ConceptNet, DBpedia, WebIsAGraph, WordNet and the Wikipedia category hierarchy.  MKGDB, thanks the versatility of the Neo4j graph database manager technology, is intended to favour and help the development of open-domain natural language processing applications relying on knowledge bases, such as information extraction, hypernymy discovery, topic clustering, and others (Camacho-Collados et al., 2018). Our resource consists of a large hypernymy graph which counts more than 37 millions nodes and more than 81 millions hypernymy relations. MKGDB and all the related material (data and source code) are publicly available under a CC BY 4.0 license 

## Authors 
Farid Yusifli, Stefano Faralli, Paola Velardi

## Resources
Download knowledge graphs that we are used from these links
[Wikipedia category](http://downloads.dbpedia.org/3.9/en/skos_categories_en.nt.bz2), [ConceptNet](https://s3.amazonaws.com/conceptnet/downloads/2019/edges/conceptnet-assertions-5.7.0.csv.gz), [DBpedia Instances](http://downloads.dbpedia.org/3.9/en/instance_types_en.nt.bz2), [WebIsAGraph](https://drive.google.com/open?id=1iNe8BcUu5Ineu3IpmjQMn2e_f3MImOLI), [Wordnet](https://wordnet.princeton.edu/download/current-version), [DBpedia Ontology](https://drive.google.com/open?id=1XwVkT40DvutyvXgyhOmUUMaW1rUJVFVc) 
  
## Installing GraphDB 
   - It is possible to download and install GraphDB that we create. You can download and unzip [ZIP](https://drive.google.com/open?id=1aREGClCuh8HRlaYya_-6Yuu2Itap4YbS) file or download [DUMP](https://drive.google.com/open?id=1Ytvzb0S1SXy3HxlitdXAnuafHqkrbJXs) file then load it using this [link](https://neo4j.com/docs/operations-manual/current/tools/dump-load/) 
   
## SETUP of Java Project
  - In order to use the source code and run it, it is enough to download and configure the config.properties file.
