# Multiple Knowledge GraphDB (MKGDB)
Farid Yusifli(1), Stefano Faralli(2), Paola Velardi(1)
(1) University of Rome Sapienza
(2) University of Rome Unitelma Sapienza

<b>MKGDB</b> is a large-scale graph database created as a combination of multiple taxonomy backbones extracted from 5 existing knowledge graphs, namely:  <i>ConceptNet</i>, <i>DBpedia</i>, <i>WebIsAGraph</i>, <i>WordNet</i> and the <i>Wikipedia category hierarchy</i>.  <b>MKGDB</b>, thanks the versatility of the <a href='https://neo4j.com/'>Neo4j</a> graph database manager technology, is intended to favour and help the development of open-domain natural language processing applications relying on knowledge bases, such as information extraction, hypernymy discovery, topic clustering, and others. Our resource consists of a large hypernymy graph which counts more than <b>37 millions nodes</b> and more than <b>81 millions hypernymy relations</b>. 

## Installing the Multiple Knowledge Graph Database 
<b>MKGDB</b> is avaliable in two format: 
<ul>
<li>The compressed zip copy of the graph database is avaliable at: https://drive.google.com/open?id=1aREGClCuh8HRlaYya_-6Yuu2Itap4YbS) file or download; 
<li>A dump of the graph databse  is avaliable at: https://drive.google.com/open?id=1Ytvzb0S1SXy3HxlitdXAnuafHqkrbJXs.</li>
</ul>
If you chose to download the copy of the database instance, you will need to modify the <i>neo4j.conf</i> file to target the download graph database and restart the neo4j server. Instead, if you want to use to load the dump version, you can follow the instructions provided at: https://neo4j.com/docs/operations-manual/current/tools/dump-load/.

## Related Resources
In this section we provide the link to download the dataset we used to generate the MKGDB:
<ul>
<li>[Wikipedia category](http://downloads.dbpedia.org/3.9/en/skos_categories_en.nt.bz2);</li>
<li>[ConceptNet](https://s3.amazonaws.com/conceptnet/downloads/2019/edges/conceptnet-assertions-5.7.0.csv.gz);</li>
<li>[DBpedia Instances](http://downloads.dbpedia.org/3.9/en/instance_types_en.nt.bz2);</li>
<li>[WebIsAGraph](https://drive.google.com/open?id=1iNe8BcUu5Ineu3IpmjQMn2e_f3MImOLI);</li> 
<li>[Wordnet](https://wordnet.princeton.edu/download/current-version);</li>
<li>[DBpedia Ontology](https://drive.google.com/open?id=1XwVkT40DvutyvXgyhOmUUMaW1rUJVFVc).</li> 
 </ul>
   
## Source code
We release also the java project we developed to the generation of the graph database starting from the above mentioned dataset.
To correctly run the generation of the resource from the above dataset it is required to edit the config.properties file.

## Contacts 
Farid Yusifli, Stefano Faralli, Paola Velardi
