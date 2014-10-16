using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace XacmlProcessor
{
    [XmlRoot(Namespace="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17")]
    public class Request
    {
        [XmlAttribute]
        public string ReturnPolicyIdList="false";

        [XmlAttribute]
        public string CombinedDecision = "false";

        [XmlElement]
        public List<Attributes> Attributes = new List<Attributes>();

        public void addAttributes(Attributes attr)
        {
           Attributes.Add(attr);
        }
    }
}
