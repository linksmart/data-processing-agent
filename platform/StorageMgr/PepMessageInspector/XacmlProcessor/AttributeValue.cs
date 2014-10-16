using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace XacmlProcessor
{
    public class AttributeValue
    {
        [XmlIgnore]
        public static string stringType = "http://www.w3.org/2001/XMLSchema#string";

        [XmlAttribute]
        public string DataType { get; set; }

        [XmlText]
        public string Value { get; set; }
    }
}
