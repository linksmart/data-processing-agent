using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace XacmlProcessor
{
    public class Attributes
    {
        [XmlIgnore]
        public static string subjectCategory = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
        [XmlIgnore]
        public static string resourceCategory = "urn:oasis:names:tc:xacml:3.0:attribute-category:resource";
        [XmlIgnore]
        public static string actionCategory = "urn:oasis:names:tc:xacml:3.0:attribute-category:action";

        [XmlAttribute]
        public string Category { get; set; }

        [XmlElement]
        public List<Attribute> Attribute = new List<Attribute>();

        public void addAttribute(Attribute attr)
        {
            Attribute.Add(attr);
        }
    }
}
