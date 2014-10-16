using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel.Dispatcher;
using System.ServiceModel.Description;
using System.ServiceModel.Configuration;
using XacmlProcessor;
using System.ServiceModel.Channels;
using System.Net;
using System.Xml;
using System.Web;
using System.ServiceModel;

namespace WcfService2
{
    public class PepMessageInspector : BehaviorExtensionElement, IDispatchMessageInspector, IServiceBehavior
    {
        private const String accessDenied = "Access Denied";

        #region IDispatchMessageInspector
        public object AfterReceiveRequest(ref System.ServiceModel.Channels.Message request, System.ServiceModel.IClientChannel channel, System.ServiceModel.InstanceContext instanceContext)
        {
            //retrieve body of the message
            string body = null;
            if (!request.IsEmpty)
            {
                XmlDictionaryReader bodyReader = request.GetReaderAtBodyContents();
                body = bodyReader.ReadInnerXml();
            }
            //retrive sender of the message
            string client = null;
            if (OperationContext.Current != null && OperationContext.Current.IncomingMessageProperties != null)
            {
                client = (OperationContext.Current.IncomingMessageProperties[RemoteEndpointMessageProperty.Name] as RemoteEndpointMessageProperty).Address;
            }
            if (client == null && HttpContext.Current != null && HttpContext.Current.Request != null)
            {
                client = HttpContext.Current.Request.UserHostAddress;
            }

            //pass request for evaluation
            bool decision = PolicyEnforcementPoint.validateRequest(
                ((HttpRequestMessageProperty)request.Properties[HttpRequestMessageProperty.Name]).Method,
                request.Headers.To.PathAndQuery,
                client,
                body);

            //if allowed we continue with the processing
            if (decision)
            {
                return null;
            }
            //if forbidden we pass a status object to identify this on reply
            else
            {
                request = null;
                return accessDenied;
            }
        }

        public void BeforeSendReply(ref System.ServiceModel.Channels.Message reply, object correlationState)
        {
            var state = correlationState as String;
            //if request was already denied on request we indicate forbidden to the client and abort
            if (state.Equals(accessDenied))
            {
                HttpResponseMessageProperty responseProperty = new HttpResponseMessageProperty();
                responseProperty.StatusCode = HttpStatusCode.Forbidden;
                reply.Properties["httpResponse"] = responseProperty;
                return;
            }
        }
        #endregion

        #region IServiceBehavior
        public void AddBindingParameters(ServiceDescription serviceDescription, System.ServiceModel.ServiceHostBase serviceHostBase, System.Collections.ObjectModel.Collection<ServiceEndpoint> endpoints, System.ServiceModel.Channels.BindingParameterCollection bindingParameters)
        {
        }

        public void ApplyDispatchBehavior(ServiceDescription serviceDescription, System.ServiceModel.ServiceHostBase serviceHostBase)
        {
            //add inspector to all the endpoints
            foreach (ChannelDispatcher chDisp in serviceHostBase.ChannelDispatchers)
            {
                foreach (EndpointDispatcher epDisp in chDisp.Endpoints)
                {
                    epDisp.DispatchRuntime.MessageInspectors.Add(this);
                }
            }
        }

        public void Validate(ServiceDescription serviceDescription, System.ServiceModel.ServiceHostBase serviceHostBase)
        {
        }
        #endregion

        #region BehaviorExtensionElement
        public override Type BehaviorType
        {
            get { return typeof(PepMessageInspector); }
        }

        protected override object CreateBehavior()
        {
            return this;
        }
        #endregion
    }
}
