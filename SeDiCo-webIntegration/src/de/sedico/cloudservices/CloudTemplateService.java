package de.sedico.cloudservices;

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.location.reference.LocationConstants;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.sshj.config.SshjSshClientModule;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;



@ManagedBean(name="cloudTemplateService")
@SessionScoped

/**
 * Die Klasse stellt die Vorlage für den Cloud-Service dar, welche  das Interface Serializable implementiert.
 * @author jens
 *
 */
public class CloudTemplateService implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CloudTemplateService.class);
	
	private String amaAccessKey;
	private String amaUserKey;
	private String ecaAccessKey;
	private String ecaUserKey;
	private String ownerId;
	
	private String provider;
	
	List<NodeMetadata> myNodesAma;
	List<Image> myImagesAma;
	List<Template> myTemplatesAma;
	
	List<NodeMetadata> myNodesEuca;
	List<Image> myImagesEuca;
	List<Template> myTemplatesEuca;
	
	private String selectedTemplateAma1;
	private NodeMetadata selectedNodeAma1;
	private Image selectedImageAma1;
	private String selectedTemplateAma2;
	private NodeMetadata selectedNodeAma2;
	private Image selectedImageAma2;
	
	private String selectedTemplateEuca1;
	private NodeMetadata selectedNodeEuca1;
	private Image selectedImageEuca1;
	private String selectedTemplateEuca2;
	private NodeMetadata selectedNodeEuca2;
	private Image selectedImageEuca2;
	
	private int foundImages;
	
	private String euca1os;
	private int euca1ram;
	private int euca1procs;
	private int euca1harddisk;
	
	private String euca2os;
	private int euca2ram;
	private int euca2procs;
	private int euca2harddisk;
	
	private String amazon1os;
	private int amazon1ram;
	private int amazon1procs;
	private int amazon1harddisk;
	
	private String amazon2os;
	private int amazon2ram;
	private int amazon2procs;
	private int amazon2harddisk;
	
	private List<String> database = new ArrayList<String>(Arrays.asList("MySql 5", "Oracle Express 11g"));
	
	
	private String selectedDatabase;
	private boolean showCloudTab = false;
/**
 * Diese Methode ermöglicht das Einloggen in die Cloud.
 * @return null
 */
	public String cloudLogin() {

		try {
			
			//getUserInstances();
			findCloudResources();
			setShowCloudTab(true);	
			
			return null;
			
		}
		catch(AuthorizationException ae) {
			FacesMessage facesMessage = new FacesMessage("Wrong Credentials");
			FacesContext.getCurrentInstance().addMessage("ama_wrongCreds",  facesMessage);
			
			return null;

		}
		catch (NoSuchElementException nse) {
			FacesMessage facesMessage = new FacesMessage("No Templates found..." +nse.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);
			return null;
		}
		
	
	}
	
	/**
	 * Diese Methode findet die nötige Ressourcen um den Cloud-Server zu instanziieren.
	 * @return null
	 * @throws NoSuchElementException - falls keine Ressourcen vorhanden sind
	 */
	public String findCloudResources() throws NoSuchElementException {
		//code to find existing instances and templates that could be used/instantiated as cloud provider
		ComputeService computeService = getComputeServiceContext(true);
		myNodesAma = new ArrayList<NodeMetadata>();
		myImagesAma = new ArrayList<Image>();
		myTemplatesAma = new ArrayList<Template>();
		
		/*
		Set<? extends Image> imageList = computeService.listImages();
		
		for (Image img : imageList) {
			log.info("Image ID: " +img.getId());
			log.info("Image Name: " +img.getName());
			log.info("Image ProviderID: " +img.getProviderId());
			log.info("Image OS: " +img.getOperatingSystem());
			Set<String> tags = img.getTags();
			for (String s : tags) {
				log.info(s);
			}
			
			myImages.add(img);
			
		}
		
		log.info("CloudTemplateService: NumberOfImages = " +imageList.size());
		foundImages = imageList.size();
		*/
		
		
		for (ComputeMetadata nodes : computeService.listNodes()) {
			NodeMetadata metadata = computeService.getNodeMetadata(nodes.getId());
			
			myNodesAma.add(metadata);
			/*
			System.out.println("======================NODE META-DATA==========================");
			System.out.println("ID: " +metadata.getId());
			System.out.println("Provider-ID: " +metadata.getProviderId());
			System.out.println("Location: " +metadata.getLocation());
			System.out.println("Name: " +metadata.getName());
			System.out.println("Group: " +metadata.getGroup());// if part of a nodeset, this identifies which one.
			System.out.println("Hardware: " +metadata.getHardware());
			System.out.println("Image-ID: " +metadata.getImageId()); // if the node was created by an image, what is its id?
			System.out.println("OS: " +metadata.getOperatingSystem());
			System.out.println("State: " +metadata.getState());
			System.out.println("Private Addresses: " +metadata.getPrivateAddresses());
			System.out.println("Public Addresses: " +metadata.getPublicAddresses());
			System.out.println("Credentials: " +metadata.getCredentials());// only available after createNodesInGroup, identifies login user/credential
			System.out.println("===============================================================");
			*/
		}
		log.info("CloudTemplateService: Found nodes = " +myNodesAma.size());
		log.info("CloudTemplateService: OS:" + amazon1os +"RAM: " + amazon1ram + " PROCS" + amazon1procs + " HD: " +amazon1harddisk);
		Template template = null;
		/*
		 	switch (amazon1os) {
		
			case "UBUNTU" : 
				template = computeService.templateBuilder().osFamily(OsFamily.UBUNTU).minRam(amazon1ram).minCores(amazon1procs).minDisk(amazon1harddisk).build();
				break;
				
			case "WINDOWS":
				template = computeService.templateBuilder().osFamily(OsFamily.WINDOWS).minRam(amazon1ram).minCores(amazon1procs).minDisk(amazon1harddisk).build();
				break;
				
			case "DEBIAN":
				template = computeService.templateBuilder().osFamily(OsFamily.DEBIAN).minRam(amazon1ram).minCores(amazon1procs).minDisk(amazon1harddisk).build();
				break;
				
			case "CENTOS": 	
				template = computeService.templateBuilder().osFamily(OsFamily.CENTOS).minRam(amazon1ram).minCores(amazon1procs).minDisk(amazon1harddisk).build();
				break;
				
			default: template = computeService.templateBuilder().osFamily(OsFamily.UBUNTU).minRam(amazon1ram).minCores(amazon1procs).minDisk(amazon1harddisk).build();
		}
		 
	
		Hardware hardware = template.getHardware();
		System.out.println("===============TEMPLATE DATA=======================");
		System.out.println("ID: " + hardware.getId());
		System.out.println("Hypervisor: " + hardware.getHypervisor());		
		System.out.println("Name: " + hardware.getName());
		System.out.println("RAM: " + hardware.getRam());
		
		//System.out.println("Location: " + hardware.getLocation().toString());
		ComputeType[] ct = hardware.getType().values();
		for (int j=0; j<ct.length; j++) {
			System.out.println("Type-Name: " +ct[j].name());
		}
		Iterator<? extends Processor> processorIterator = hardware.getProcessors().iterator();
		while(processorIterator.hasNext()) {
			Processor p = processorIterator.next(); 
			System.out.println("Processor Cores: " +p.toString());
		}
		myTemplatesAma.add(template);
		*/
		computeService.getContext().close();
		
		return null;
		
	}
	/*
	public String getUserInstances() throws AuthorizationException {
		myNodes = new ArrayList<NodeMetadata>();
		
		ComputeService computeService = getAmazonComputeServiceContext(true);
		

		for (ComputeMetadata node : computeService.listNodes()) {
			NodeMetadata metadata = computeService.getNodeMetadata(node.getId());
			log.info("======================NODE META-DATA==========================");
			log.info("ID: " +metadata.getId());
			log.info("Provider-ID: " +metadata.getProviderId());
			log.info("Location: " +metadata.getLocation());
			log.info("Name: " +metadata.getName());
			log.info("Group: " +metadata.getGroup());// if part of a nodeset, this identifies which one.
			log.info("Hardware: " +metadata.getHardware());
			log.info("Image-ID: " +metadata.getImageId()); // if the node was created by an image, what is its id?
			log.info("OS: " +metadata.getOperatingSystem());
			log.info("Private Addresses: " +metadata.getPrivateAddresses());
			log.info("Public Addresses: " +metadata.getPublicAddresses());
			log.info("Credentials: " +metadata.getCredentials());// only available after createNodesInGroup, identifies login user/credential
			log.info("===============================================================");
			
			myNodes.add(metadata);
			
		}
		computeService.getContext().close();
		log.info("CloudTemplateService: Found nodes = " +myNodes.size());
		return null;
	}
	*/
	/**
	 * Diese Methode gibt den Context auf dem Bildschirm aus.
	 * @param logginOn - prüft, ob der Benutzer eingeloggt ist
	 * @return context.buildView(ComputeServiceContext.class).getComputeService() - Rückgabe des Contextes auf dem Bildschirm
	 */
	public ComputeService getComputeServiceContext(boolean logginOn) {
		Iterable<Module> modules;
		
		// example of injecting a ssh implementation
	    if (logginOn == true) {
	    	modules = ImmutableSet.<Module> of(
	  	          new SshjSshClientModule(), 
	  	          new SLF4JLoggingModule());
	    }
	    else {
	    	modules = ImmutableSet.<Module> of(
	  	          new SshjSshClientModule());	
	    }
		
		// example of specific properties, in this case optimizing image list to
	    // only amazon supplied
		/*
	    Properties properties = new Properties();
	    properties.setProperty(PROPERTY_EC2_AMI_QUERY, "owner-id="+ownerId+";state=available;image-type=machine");
	    properties.setProperty(PROPERTY_EC2_CC_AMI_QUERY, "");
	    long scriptTimeout = TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES);
	    properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
		*/

	    //Properties overrides = new Properties();
	    //overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY,
	    //		"state=available;architecture=x86_64;image-type=machine;is-public=true;description="+cloudOSChooseamazon1);        
	    //overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY,"");
	    //overrides.setProperty(LocationConstants.PROPERTY_REGIONS, Region.US_EAST_1);
	    log.info("CloudTemplateService: cloudOSChooseamazon1 = " +amazon1os);
	    
	    log.info("CloudServiceTemplate: getting Context Builder with = " +amaAccessKey + " " + amaUserKey);
	    
		ContextBuilder context = ContextBuilder.newBuilder("aws-ec2")
														.credentials(amaAccessKey, amaUserKey)
														.modules(modules);
														//.overrides(overrides);
		
		System.out.printf(">> initializing %s%n", context.getApiMetadata());
	
		
		
		
		return context.buildView(ComputeServiceContext.class).getComputeService();

	}
	
	

	public String getAmaAccessKey() {
		return amaAccessKey;
	}
	public void setAmaAccessKey(String amaAccessKey) {
		this.amaAccessKey = amaAccessKey;
	}
	public String getAmaUserKey() {
		return amaUserKey;
	}
	public void setAmaUserKey(String amaUserKey) {
		this.amaUserKey = amaUserKey;
	}
	public String getEcaAccessKey() {
		return ecaAccessKey;
	}
	public void setEcaAccessKey(String ecaAccessKey) {
		this.ecaAccessKey = ecaAccessKey;
	}
	public String getEcaUserKey() {
		return ecaUserKey;
	}
	public void setEcaUserKey(String ecaUserKey) {
		this.ecaUserKey = ecaUserKey;
	}
	public boolean isShowCloudTab() {
		return showCloudTab;
	}
	public void setShowCloudTab(boolean showCloudTab) {
		this.showCloudTab = showCloudTab;
	}
	public List<String> getDatabase() {
		return database;
	}
	public void setDatabase(List<String> database) {
		this.database = database;
	}
	public String getSelectedDatabase() {
		return selectedDatabase;
	}
	public void setSelectedDatabase(String selectedDatabase) {
		this.selectedDatabase = selectedDatabase;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public int getFoundImages() {
		return foundImages;
	}
	public void setFoundImages(int foundImages) {
		this.foundImages = foundImages;
	}
	public String getAmazon1os() {
		return amazon1os;
	}
	public void setAmazon1os(String amazon1os) {
		this.amazon1os = amazon1os;
	}
	public int getAmazon1procs() {
		return amazon1procs;
	}
	public void setAmazon1procs(int amazon1procs) {
		this.amazon1procs = amazon1procs;
	}
	public int getAmazon1harddisk() {
		return amazon1harddisk;
	}
	public void setAmazon1harddisk(int amazon1harddisk) {
		this.amazon1harddisk = amazon1harddisk;
	}
	public int getAmazon1ram() {
		return amazon1ram;
	}
	public void setAmazon1ram(int amazon1ram) {
		this.amazon1ram = amazon1ram;
	}
	public String getEuca1os() {
		return euca1os;
	}
	public void setEuca1os(String euca1os) {
		this.euca1os = euca1os;
	}
	public int getEuca1ram() {
		return euca1ram;
	}
	public void setEuca1ram(int euca1ram) {
		this.euca1ram = euca1ram;
	}
	public int getEuca1procs() {
		return euca1procs;
	}
	public void setEuca1procs(int euca1procs) {
		this.euca1procs = euca1procs;
	}
	public int getEuca1harddisk() {
		return euca1harddisk;
	}
	public void setEuca1harddisk(int euca1harddisk) {
		this.euca1harddisk = euca1harddisk;
	}
	public String getEuca2os() {
		return euca2os;
	}
	public void setEuca2os(String euca2os) {
		this.euca2os = euca2os;
	}
	public int getEuca2ram() {
		return euca2ram;
	}
	public void setEuca2ram(int euca2ram) {
		this.euca2ram = euca2ram;
	}
	public int getEuca2procs() {
		return euca2procs;
	}
	public void setEuca2procs(int euca2procs) {
		this.euca2procs = euca2procs;
	}
	public int getEuca2harddisk() {
		return euca2harddisk;
	}
	public void setEuca2harddisk(int euca2harddisk) {
		this.euca2harddisk = euca2harddisk;
	}
	public String getAmazon2os() {
		return amazon2os;
	}
	public void setAmazon2os(String amazon2os) {
		this.amazon2os = amazon2os;
	}
	public int getAmazon2ram() {
		return amazon2ram;
	}
	public void setAmazon2ram(int amazon2ram) {
		this.amazon2ram = amazon2ram;
	}
	public int getAmazon2procs() {
		return amazon2procs;
	}
	public void setAmazon2procs(int amazon2procs) {
		this.amazon2procs = amazon2procs;
	}
	public int getAmazon2harddisk() {
		return amazon2harddisk;
	}
	public void setAmazon2harddisk(int amazon2harddisk) {
		this.amazon2harddisk = amazon2harddisk;
	}
	public String getSelectedTemplateAma1() {
		return selectedTemplateAma1;
	}
	public void setSelectedTemplateAma1(String selectedTemplateAma1) {
		this.selectedTemplateAma1 = selectedTemplateAma1;
	}
	public NodeMetadata getSelectedNodeAma1() {
		return selectedNodeAma1;
	}
	public void setSelectedNodeAma1(NodeMetadata selectedNodeAma1) {
		this.selectedNodeAma1 = selectedNodeAma1;
	}
	public Image getSelectedImageAma1() {
		return selectedImageAma1;
	}
	public void setSelectedImageAma1(Image selectedImageAma1) {
		this.selectedImageAma1 = selectedImageAma1;
	}
	public String getSelectedTemplateAma2() {
		return selectedTemplateAma2;
	}
	public void setSelectedTemplateAma2(String selectedTemplateAma2) {
		this.selectedTemplateAma2 = selectedTemplateAma2;
	}
	public NodeMetadata getSelectedNodeAma2() {
		return selectedNodeAma2;
	}
	public void setSelectedNodeAma2(NodeMetadata selectedNodeAma2) {
		this.selectedNodeAma2 = selectedNodeAma2;
	}
	public Image getSelectedImageAma2() {
		return selectedImageAma2;
	}
	public void setSelectedImageAma2(Image selectedImageAma2) {
		this.selectedImageAma2 = selectedImageAma2;
	}
	public String getSelectedTemplateEuca1() {
		return selectedTemplateEuca1;
	}
	public void setSelectedTemplateEuca1(String selectedTemplateEuca1) {
		this.selectedTemplateEuca1 = selectedTemplateEuca1;
	}
	public NodeMetadata getSelectedNodeEuca1() {
		return selectedNodeEuca1;
	}
	public void setSelectedNodeEuca1(NodeMetadata selectedNodeEuca1) {
		this.selectedNodeEuca1 = selectedNodeEuca1;
	}
	public Image getSelectedImageEuca1() {
		return selectedImageEuca1;
	}
	public void setSelectedImageEuca1(Image selectedImageEuca1) {
		this.selectedImageEuca1 = selectedImageEuca1;
	}
	public String getSelectedTemplateEuca2() {
		return selectedTemplateEuca2;
	}
	public void setSelectedTemplateEuca2(String selectedTemplateEuca2) {
		this.selectedTemplateEuca2 = selectedTemplateEuca2;
	}
	public NodeMetadata getSelectedNodeEuca2() {
		return selectedNodeEuca2;
	}
	public void setSelectedNodeEuca2(NodeMetadata selectedNodeEuca2) {
		this.selectedNodeEuca2 = selectedNodeEuca2;
	}
	public Image getSelectedImageEuca2() {
		return selectedImageEuca2;
	}
	public void setSelectedImageEuca2(Image selectedImageEuca2) {
		this.selectedImageEuca2 = selectedImageEuca2;
	}
	public List<NodeMetadata> getMyNodesAma() {
		return myNodesAma;
	}
	public void setMyNodesAma(List<NodeMetadata> myNodesAma) {
		this.myNodesAma = myNodesAma;
	}
	public List<Image> getMyImagesAma() {
		return myImagesAma;
	}
	public void setMyImagesAma(List<Image> myImagesAma) {
		this.myImagesAma = myImagesAma;
	}
	public List<Template> getMyTemplatesAma() {
		return myTemplatesAma;
	}
	public void setMyTemplatesAma(List<Template> myTemplatesAma) {
		this.myTemplatesAma = myTemplatesAma;
	}
	public List<NodeMetadata> getMyNodesEuca() {
		return myNodesEuca;
	}
	public void setMyNodesEuca(List<NodeMetadata> myNodesEuca) {
		this.myNodesEuca = myNodesEuca;
	}
	public List<Image> getMyImagesEuca() {
		return myImagesEuca;
	}
	public void setMyImagesEuca(List<Image> myImagesEuca) {
		this.myImagesEuca = myImagesEuca;
	}
	public List<Template> getMyTemplatesEuca() {
		return myTemplatesEuca;
	}
	public void setMyTemplatesEuca(List<Template> myTemplatesEuca) {
		this.myTemplatesEuca = myTemplatesEuca;
	}
	
}
