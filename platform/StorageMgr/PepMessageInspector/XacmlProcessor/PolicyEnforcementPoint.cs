using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using System.Net;
using System.Xml;

namespace XacmlProcessor
{
    public class PolicyEnforcementPoint
    {
        public static bool validateRequest(string httpMethod, string to, string from, string body)
        {
            //compose request
            var request = new Request();

            if (!String.IsNullOrEmpty(from))
            {
                Attribute subj = new Attribute() { AttributeId = Attribute.subjectId};
                subj.setValue(new AttributeValue(){DataType = AttributeValue.stringType, Value = from});
                Attributes subjCat = new Attributes() { Category = Attributes.subjectCategory };
                subjCat.addAttribute(subj);
                request.addAttributes(subjCat);
            }

            Attribute resource = new Attribute() { AttributeId = Attribute.resourceId };
            resource.setValue(new AttributeValue() { DataType = AttributeValue.stringType, Value = to });
            Attributes rescCat = new Attributes() { Category = Attributes.resourceCategory };
            rescCat.addAttribute(resource);
            request.addAttributes(rescCat);

            Attribute action = new Attribute() { AttributeId = Attribute.actionId};
            action.setValue(new AttributeValue() { DataType = AttributeValue.stringType, Value = httpMethod });
            Attributes actCat = new Attributes() { Category = Attributes.actionCategory };
            actCat.addAttribute(action);
            request.addAttributes(actCat);

            //serialize request into string
            StringBuilder sb = new StringBuilder();
            var serializer = new XmlSerializer(typeof(Request));
            serializer.Serialize(XmlWriter.Create(sb, new XmlWriterSettings { OmitXmlDeclaration = true, Indent = false, NewLineHandling = NewLineHandling.Replace, NewLineChars = "\n" }), request);
            string requestString = sb.ToString();

            //send request to PDP
            PDP.PolicyDecisionPointPortTypeClient pdpClient = new PDP.PolicyDecisionPointPortTypeClient("PolicyDecisionPointPort");
            string response = pdpClient.evaluate(requestString);
            response += "test";



            return false;
        }
    }
}
