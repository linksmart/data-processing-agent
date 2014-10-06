/*
 * SmartCityAPI - KML to N3 conversion
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pertlab.smartcity.data.n3.deserialization;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bonino
 *
 */
public class N3DeserializationHelper
{
	// The ontology manager
	private OWLOntologyManager ontologyManager;
	
	// The default prefix manager
	private DefaultPrefixManager prefixManager;
	
	// The owl ontology model
	private OWLOntology ontModel;
	
	// The class logger
	private Logger logger;
	
	/**
	 * 
	 */
	public N3DeserializationHelper()
	{
		// get the class logger
		this.logger = LoggerFactory.getLogger(N3Parser.class);
		
		// build an instance of OWLManager for loading the N3 data model
		this.ontologyManager = OWLManager.createOWLOntologyManager();
		
		// create the default prefix manager
		this.prefixManager = new DefaultPrefixManager();
	}
	
	/**
	 * @return the ontologyManager
	 */
	public OWLOntologyManager getOntologyManager()
	{
		return ontologyManager;
	}
	
	/**
	 * @return the prefixManager
	 */
	public DefaultPrefixManager getPrefixManager()
	{
		return prefixManager;
	}
	
	/**
	 * @return the ontModel
	 */
	public OWLOntology getOntModel()
	{
		return ontModel;
	}
	
	public void addLocalOntology(String prefix, String ontologyURL, String ontologyLocation)
	{
		// check that prefix and location are not null
		if ((prefix != null) && (!prefix.isEmpty()) && (ontologyLocation != null) && (!ontologyLocation.isEmpty())
				&& (ontologyURL != null) && (!ontologyURL.isEmpty()))
		{
			// check the ontology location
			File ontologyFile = new File(ontologyLocation);
			
			// check exists
			if (ontologyFile.exists())
			{
				// handle prefix
				
				// add a custom IRI mapper
				this.ontologyManager
						.addIRIMapper(new SimpleIRIMapper(IRI.create(ontologyURL), IRI.create(ontologyFile)));
				
				// add the prefix mapping
				this.prefixManager.setPrefix(prefix + ":", ontologyURL + "#");
			}
		}
	}
	
	public void loadOntology(String ontologyURL)
	{
		try
		{
			this.ontModel = this.ontologyManager.loadOntology(IRI.create(ontologyURL));
			
			// debug
			this.logger.info("Loaded model" + this.ontModel.getOntologyID().getOntologyIRI() + ": "
					+ this.ontModel.getAxiomCount() + " axioms.");
			
			// set the prefixes of the declared ontologies
			
			PrefixOWLOntologyFormat pf = this.ontologyManager.getOntologyFormat(this.ontModel)
					.asPrefixOWLOntologyFormat();
			for (String prefix : pf.getPrefixNames())
			{
				if (!this.prefixManager.containsPrefixMapping(prefix)
						&& !this.prefixManager.getPrefixName2PrefixMap().containsValue(pf.getPrefix(prefix)))
					this.prefixManager.setPrefix(prefix, pf.getPrefix(prefix));
			}
		}
		catch (OWLOntologyCreationException e)
		{
			this.logger.warn("Unable to load the N3 model", e);
		}
	}
	
	public Map<String, Object> getAnnotationValues(OWLNamedIndividual individual)
	{
		Map<String, Object> annotationValuesMap = new HashMap<String, Object>();
		
		Set<OWLAnnotation> annotations = individual.getAnnotations(this.ontModel);
		
		for (OWLAnnotation annotation : annotations)
		{
			OWLAnnotationValue annotationValue = annotation.getValue();
			
			if (annotationValue instanceof OWLLiteral)
			{
				// this.logger.debug(annotation.getValue().toString());
				
				String annotationName = annotation.getProperty().getIRI().getShortForm();
				OWL2Datatype annotationType = ((OWLLiteral) annotationValue).getDatatype().getBuiltInDatatype();
				if (annotationType != null)
				{
					switch (annotationType)
					{
						case XSD_DECIMAL:
						case XSD_DOUBLE:
						{
							annotationValuesMap.put(annotationName, ((OWLLiteral) annotationValue).parseDouble());
							break;
						}
						case XSD_BOOLEAN:
						{
							annotationValuesMap.put(annotationName, ((OWLLiteral) annotationValue).parseBoolean());
							break;
						}
						case XSD_FLOAT:
						{
							annotationValuesMap.put(annotationName, ((OWLLiteral) annotationValue).parseFloat());
							break;
						}
						case XSD_INT:
						case XSD_INTEGER:
						{
							annotationValuesMap.put(annotationName, ((OWLLiteral) annotationValue).parseInteger());
						}
						default:
						{
							annotationValuesMap.put(annotationName, ((OWLLiteral) annotationValue).getLiteral());
						}
					}
				}
			}
		}
		
		return annotationValuesMap;
	}
	
	public Map<String, Set<OWLIndividual>> getObjectPropertyValues(OWLNamedIndividual individual)
	{
		// prepare the map for holding the individuals pointed by object
		// properties attached to the given individual
		Map<String, Set<OWLIndividual>> objectPropertyObjects = new HashMap<String, Set<OWLIndividual>>();
		
		// the all the object property values for the given individual
		Map<OWLObjectPropertyExpression, Set<OWLIndividual>> propertyValues = individual
				.getObjectPropertyValues(this.ontModel);
		
		// store the extracted values, by property name
		for (OWLObjectPropertyExpression propertyExpression : propertyValues.keySet())
		{
			//get all the individuals associated to the given object property
			Set<OWLIndividual> individuals = propertyValues.get(propertyExpression);
			
			//store the set
			objectPropertyObjects.put(propertyExpression.asOWLObjectProperty().getIRI().getShortForm(), individuals);
			
		}
		
		return objectPropertyObjects;
	}
	
	public Set<OWLNamedIndividual> getAllIndividuals(String type)
	{
		Set<OWLNamedIndividual> allIndividuals = new HashSet<OWLNamedIndividual>();
		if ((type != null) && (!type.isEmpty()))
		{
			OWLDataFactory df = this.ontologyManager.getOWLDataFactory();
			IRI individualClassIRI = IRI.create(this.prefixManager.getPrefix("wbin:")
					 + type);
			OWLClass individualClass = df.getOWLClass(individualClassIRI);
			
			this.logger.debug(individualClassIRI.toString());
			Set<OWLIndividual> individuals = individualClass.getIndividuals(this.ontModel);
			
			// fill the set to return, considering named individuals only
			for (OWLIndividual individual : individuals)
			{
				this.logger.debug(individual.toStringID());
				if (individual.isNamed())
					allIndividuals.add(individual.asOWLNamedIndividual());
			}
		}
		else
		{
			// extract all individuals
			allIndividuals.addAll(this.ontModel.getIndividualsInSignature());
		}
		
		return allIndividuals;
	}
}
