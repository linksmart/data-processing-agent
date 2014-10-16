using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace XacmlProcessor
{
    public class Attribute
    {
        [XmlIgnore]
        public static string subjectId = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
        [XmlIgnore]
        public static string resourceId = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
        [XmlIgnore]
        public static string actionId = "urn:oasis:names:tc:xacml:1.0:action:action-id";

        [XmlAttribute]
        public string AttributeId { get; set; }

        [XmlAttribute]
        public string IncludeInResult = "true";

        [XmlElement]
        public AttributeValue[] AttributeValue = new AttributeValue[1];

        public void setValue(AttributeValue value)
        {
            AttributeValue[0] = value;
        }
    }
}
