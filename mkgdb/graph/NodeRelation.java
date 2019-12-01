package mkgdb.graph;

public class NodeRelation
{
    private String hyponym;
    private String hypernym;
    private String knowledgeBase;

    public NodeRelation(String hyponym, String hypernym, String knowledgeBase) 
    {
        this.hypernym = hypernym;
        this.hyponym = hyponym;
        this.knowledgeBase = knowledgeBase;
    }

    public String getHyponym()
    {
        return this.hyponym;
    }

    public String getHypernym()
    {
        return this.hypernym;
    }

    public String getKnowledgeBase()
    {
        return this.knowledgeBase;
    }

}