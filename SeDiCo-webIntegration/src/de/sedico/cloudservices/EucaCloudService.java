package de.sedico.cloudservices;

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jclouds.ContextBuilder;
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
import org.jclouds.eucalyptus.EucalyptusApiMetadata;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import de.sedico.generictableadapter.TableColHeader;
import de.sedico.generictableadapter.TreeBean;
import de.sedico.interfaces.ICloudService;
import de.sedico.partition.Configuration;
import de.sedico.partition.SQLServers;
import de.sedico.sql.Table;
import de.sedico.sql.importing.SQLImportStrategyBase;
import de.sedico.tools.SeDiCoConstants;
import de.sedico.tools.SeDiCoFileWriter;

@ManagedBean(name = "eucaCloudService")
@SessionScoped

/**
 * Diese Klasse implementiert den Cloud-Service der Eucalyptus-Cloud. Sie implementiert das Interface Serializable, ICloudService.
 * @author jens
 *
 */
public class EucaCloudService implements Serializable, ICloudService {
	private static Logger log = Logger.getLogger(EucaCloudService.class);
	private static final long serialVersionUID = 1L;

	private String ecaAccessKey;
	private String ecaUserKey;

	List<NodeMetadata> myNodesEuca;
	List<Image> myImagesEuca;
	List<Template> myTemplatesEuca;

	private int foundImages;

	private String euca1os;
	private int euca1ram;
	private int euca1procs;
	private int euca1harddisk;

	private String euca2os;
	private int euca2ram;
	private int euca2procs;
	private int euca2harddisk;

	private String selectedDatabase;
	private List<String> database = new ArrayList<String>(Arrays.asList(
			"MySql 5", "Oracle Express 11g"));

	private String selectedTemplateEuca1;
	private NodeMetadata selectedNodeEuca1;
	private Image selectedImageEuca1;
	private String selectedTemplateEuca2;
	private NodeMetadata selectedNodeEuca2;
	private Image selectedImageEuca2;

	private boolean showCloudTab = false;

	private String selectedImageEca1;
	private String selectedImageEca2;

	private String publicIp1;
	private int port1;
	private String dbName1;
	private String partitionName1;
	private String dbUser1;
	private String dbPassword1;
	private String dbType1;

	private String publicIp2;
	private int port2;
	private String dbName2;
	private String partitionName2;
	private String dbUser2;
	private String dbPassword2;
	private String dbType2;

	private String selectedEca1ImageId;
	private String selectedEca2ImageId;

	public String getSelectedEca2ImageId() {
		return selectedEca2ImageId;
	}

	public void setSelectedEca2ImageId(String selectedEca2ImageId) {
		this.selectedEca2ImageId = selectedEca2ImageId;
	}

	private boolean inactivateFieldsGUI1 = false;
	private boolean inactivateFieldsGUI2 = false;

	public boolean isInactivateFieldsGUI1() {
		return inactivateFieldsGUI1;
	}

	public void setInactivateFieldsGUI1(boolean inactivateFieldsGUI1) {
		this.inactivateFieldsGUI1 = inactivateFieldsGUI1;
	}

	public boolean isInactivateFieldsGUI2() {
		return inactivateFieldsGUI2;
	}

	public void setInactivateFieldsGUI2(boolean inactivateFieldsGUI2) {
		this.inactivateFieldsGUI2 = inactivateFieldsGUI2;
	}

	public String getSelectedEca1ImageId() {
		return selectedEca1ImageId;
	}

	public void setSelectedEca1ImageId(String selectedEca1ImageId) {
		this.selectedEca1ImageId = selectedEca1ImageId;
	}

	public EucaCloudService() {

	}
	/**
	 * Die Methode ermöglicht den Login in die Cloud. 
	 * @throws AuthorizationException - falls die Autorisierung fehlschlägt
	 * @throws NoSuchElementException - falls keine Ressourcen der Cloud vorhanden sind
	 */
	@Override
	public String cloudLogin() throws AuthorizationException,
			NoSuchElementException {
		try {

			findCloudResources();
			setShowCloudTab(true);

			return null;

		} catch (AuthorizationException ae) {
			FacesMessage facesMessage = new FacesMessage("Wrong Credentials");
			FacesContext.getCurrentInstance().addMessage("eca_wrongCreds",
					facesMessage);

			return null;

		} catch (NoSuchElementException nse) {
			FacesMessage facesMessage = new FacesMessage(
					"No Templates found..." + nse.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, facesMessage);
			return null;
		}

	}
/**
 * Diese Methode sucht die Ressourcen zur Instanziierung der Cloud
 * @return null
 */
	@Override
	public String findCloudResources() {
		ComputeService computeService = getComputeServiceContext(true);
		log.info("ComputeService OK");

		myNodesEuca = new ArrayList<NodeMetadata>();
		myImagesEuca = new ArrayList<Image>();
		myTemplatesEuca = new ArrayList<Template>();

		for (ComputeMetadata nodes : computeService.listNodes()) {
			log.info("ListNodes");
			NodeMetadata metadata = computeService.getNodeMetadata(nodes
					.getId());
			log.info("Iterating through node data");
			myNodesEuca.add(metadata);

		}
		computeService.getContext().close();
		return null;
		/*
		 * Set<? extends Image> myImages = computeService.listImages();
		 * 
		 * Iterator<? extends Image> i = myImages.iterator();
		 * 
		 * while (i.hasNext()) { Image image = (Image) i.next();
		 * System.out.println("Image ID: " +image.getId());
		 * System.out.println("Image Name: " +image.getName());
		 * System.out.println("Image ProviderID: " +image.getProviderId());
		 * System.out.println("Image Version: " +image.getVersion());
		 * //System.out.println("Image AdminPW: " +image.getAdminPassword());
		 * System.out.println("Image OS: " +image.getOperatingSystem());
		 * System.out.println("Image Type: " +image.getType()); }
		 * 
		 * List<NodeMetadata> metas = new ArrayList<NodeMetadata>();
		 * 
		 * for (ComputeMetadata nodes : computeService.listNodes()) {
		 * NodeMetadata metadata =
		 * computeService.getNodeMetadata(nodes.getId());
		 * 
		 * 
		 * log.info("ID: " +nodes.getId street zip regCust solScore Partition 1
		 * Id name creditCard Partition 2());
		 * log.info("======================NODE META-DATA=========================="
		 * ); log.info("ID: " +metadata.getId()); log.info("Provider-ID: "
		 * +metadata.getProviderId()); log.info("Location: "
		 * +metadata.getLocation()); log.info("Name: " +metadata.getName());
		 * log.info("Group: " +metadata.getGroup());// if part of a nodeset,
		 * this identifies which one. log.info("Hardware: "
		 * +metadata.getHardware()); log.info("Image-ID: "
		 * +metadata.getImageId()); // if the node was created by an image, what
		 * is its id? log.info("OS: " +metadata.getOperatingSystem());
		 * //System.out.println("State: " +metadata.getState());
		 * log.info("Private Addresses: " +metadata.getPrivateAddresses());
		 * log.info("Public Addresses: " +metadata.getPublicAddresses());
		 * log.info("Credentials: " +metadata.getCredentials());// only
		 * available after createNodesInGroup, identifies login user/credential
		 * log
		 * .info("==============================================================="
		 * );
		 * 
		 * myNodesEuca.add(metadata); }
		 */

	}

	/*
	 * catch(IllegalStateException ise) { log.info("IllegalStateException: "
	 * +ise.getMessage()); return null; } catch(NoSuchElementException nse) {
	 * log.info("NoSuchElementException: " +nse.getMessage()); return null; }
	 * 
	 * 
	 * finally { log.info("Finally Block"); computeService.getContext().close();
	 * 
	 * 
	 * }
	 * 
	 * return null;
	 * 
	 * 
	 * 
	 * /*
	 * 
	 * //code to find eucalyptus templates Template template = null; switch
	 * (euca1os) { case "UBUNTU" : template =
	 * computeService.templateBuilder().osFamily
	 * (OsFamily.UBUNTU).minRam(euca1ram
	 * ).minCores(euca1procs).minDisk(euca1harddisk).build(); break;
	 * 
	 * case "WINDOWS": template =
	 * computeService.templateBuilder().osFamily(OsFamily
	 * .WINDOWS).minRam(euca1ram
	 * ).minCores(euca1procs).minDisk(euca1harddisk).build(); break;
	 * 
	 * case "DEBIAN": template =
	 * computeService.templateBuilder().osFamily(OsFamily
	 * .DEBIAN).minRam(euca1ram
	 * ).minCores(euca1procs).minDisk(euca1harddisk).build(); break;
	 * 
	 * case "CENTOS": template =
	 * computeService.templateBuilder().osFamily(OsFamily
	 * .CENTOS).minRam(euca1ram
	 * ).minCores(euca1procs).minDisk(euca1harddisk).build(); break;
	 * 
	 * default: template =
	 * computeService.templateBuilder().osFamily(OsFamily.UBUNTU
	 * ).minRam(euca1ram).minCores(euca1procs).minDisk(euca1harddisk).build(); }
	 * 
	 * 
	 * Hardware hardware = template.getHardware();
	 * System.out.println("===============TEMPLATE DATA======================="
	 * ); System.out.println("ID: " + hardware.getId());
	 * System.out.println("Hypervisor: " + hardware.getHypervisor());
	 * System.out.println("Name: " + hardware.getName());
	 * System.out.println("RAM: " + hardware.getRam());
	 * 
	 * //System.out.println("Location: " + hardware.getLocation().toString());
	 * ComputeType[] ct = hardware.getType().values(); for (int j=0;
	 * j<ct.length; j++) { System.out.println("Type-Name: " +ct[j].name()); }
	 * Iterator<? extends Processor> processorIterator =
	 * hardware.getProcessors().iterator(); while(processorIterator.hasNext()) {
	 * Processor p = processorIterator.next();
	 * System.out.println("Processor Cores: " +p.toString()); }
	 * myTemplatesEuca.add(template);
	 */

	// code to find eucalyptus images
	// Set<? extends Image> imageList = computeService.listImages();

	/*
	 * for (Image img : imageList) { log.info("Image ID: " +img.getId());
	 * log.info("Image Name: " +img.getName()); log.info("Image ProviderID: "
	 * +img.getProviderId()); log.info("Image OS: " +img.getOperatingSystem());
	 * Set<String> tags = img.getTags(); for (String s : tags) { log.info(s); }
	 * 
	 * myImagesEuca.add(img);
	 * 
	 * }
	 * 
	 * 
	 * log.info("CloudTemplateService: NumberOfImages = " +imageList.size());
	 * foundImages = imageList.size();
	 */
	/**
	 * Die Methode gibt den Context auf dem Bildschirm aus.
	 * @param logginOn - prüft, ob der Nutzer eingeloggt ist.
	 * @return context.buildView(ComputeServiceContext.class)
				.getComputeService(); - Bildschrimausgabe
	 */
	@Override
	public ComputeService getComputeServiceContext(boolean logginOn) {
		Iterable<Module> modules;
		Properties properties = new Properties();
		// properties.setProperty("jclouds.regions", "aws-ec2");
		// properties.setProperty(PROPERTY_EC2_CC_AMI_QUERY,
		// "state=available;image-type=machine");
		// properties.setProperty(PROPERTY_EC2_AMI_QUERY,
		// "state=available;image-type=machine");

		// example of injecting a ssh implementation
		if (logginOn == true) {
			modules = ImmutableSet.<Module> of(new SshjSshClientModule(),
					new SLF4JLoggingModule());
		} else {
			modules = ImmutableSet.<Module> of(new SshjSshClientModule());
		}
		/*
		 * ComputeServiceContext context = ContextBuilder.newBuilder(new
		 * EucalyptusApiMetadata()) .credentials("5NE9NHJVHGKN3O6SA5UG6",
		 * "ef0Tpnx6mDzZtEMYlqEqCWRtF7lMj20CjoF4IU3J") .overrides(properties)
		 * .modules(modules) //Eucalyptus Private HS-Cloud
		 * .endpoint("http://141.19.142.15:8773/services/Eucalyptus/")
		 * //.endpoint
		 * ("http://eucalyptus.hscloud.frontend:8773/services/Eucalyptus/")
		 * //Eucalyptus Communiy Cloud //.endpoint(
		 * "http://communitycloud.eucalyptus.com:8773/services/Eucalyptus/")
		 * .build(ComputeServiceContext.class);
		 */
		// HSCloud 141.19.142.15
		ContextBuilder context = ContextBuilder
				.newBuilder(new EucalyptusApiMetadata())
				.credentials(ecaAccessKey, ecaUserKey)
				.modules(modules)
				.endpoint("http://141.19.142.15:8773/services/Eucalyptus/")
				.overrides(properties);

		return context.buildView(ComputeServiceContext.class)
				.getComputeService();
		/*
		 * //HSCloud 141.19.146.245 ComputeServiceContext context =
		 * ContextBuilder.newBuilder(new EucalyptusApiMetadata())
		 * .credentials("accessKey", "userKey") .overrides(properties)
		 * .modules(modules)
		 * .endpoint("http://141.19.146.245:8773/services/Eucalyptus/")
		 * .build(ComputeServiceContext.class);
		 * 
		 * //log.info("Returning context");
		 */
		// return context.getComputeService();

	}
/**
 * Diese Methode lädt die Instanzdaten der ersten Partition.
 * @return null
 */
	public String loadInstanceData1() {
		log.info("Entering EUCA loadInstanceData1");

		String imageId = selectedEca1ImageId;
		log.info("ECA ImageID: " + imageId);
		// log.info(selectedImageAma1.getId());

		Iterator<NodeMetadata> i = myNodesEuca.iterator();
		while (i.hasNext()) {
			NodeMetadata m = i.next();
			if (m.getId().equals(selectedEca1ImageId)) {

				String publicIPList = m.getPublicAddresses().toString();
				publicIPList = publicIPList.replace("[", "");
				publicIPList = publicIPList.replace("]", "");
				publicIPList = publicIPList.trim();

				publicIp1 = publicIPList;
				log.info("New ECA IP: " + publicIp1);
				break;

			}
			// log.info("Meta-Data ID: " +m.getId());
		}


		
		FacesContext fc = FacesContext.getCurrentInstance();

		Map<String, String> params = fc.getExternalContext()
				.getRequestParameterMap();

		log.info("publicIp1: " + publicIp1);
		log.info("port1: " + port1);
		log.info("dbName1: " + dbName1);
		log.info("partitionName1: " + partitionName1);
		log.info("dbUser1: " + dbUser1);
		log.info("dbPassword1: " + dbPassword1);
		log.info("DBType: " + dbType1);

		// getting the table headers of the first partition from
		// chooseClouds.xhtml
		log.info("getting TreeBean from Context");
		TreeBean sb = TreeBean.getCurrentInstance();

		log.info("Partition1 Size: "
				+ sb.getTableColHeaderListPartition1().size());

		HttpServletResponse response = (HttpServletResponse) fc
				.getExternalContext().getResponse();

		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();
		String rootPath = ctx.getRealPath("/");

		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();
/*
		String path = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName()
				+ "/amazonPartition1Data.txt";
		File file = new File(path);
*/
		String sedicoConfigPath = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName() + "/sedico.config";
		File sedicoConfig = new File(sedicoConfigPath);

		sedicoConfig.getParentFile().mkdirs();

		// use these variables to write the values into Patrick's config file
		// like in the code below
		try {

			// write the sedico.config file
			FileWriter fileConfigStream = new FileWriter(sedicoConfigPath);
			BufferedWriter configOut = new BufferedWriter(fileConfigStream);

			// FacesUtil.lookupManagedBeanName("treeBean");
			TreeBean tb = TreeBean.getCurrentInstance();

			String user = tb.getUser();
			String password = tb.getPassword();
			String host = tb.getHost();
			int port = tb.getPort();
			String chosenTable = tb.getChosenTable();
			String chosenDatabase = tb.getChosenDatabase();
			String sqlType = tb.getSqlType();

			if (sqlType.toLowerCase().equals("mysql"))
				sqlType = SQLServers.MySQL.toString();
			else
				sqlType = SQLServers.Oracle.toString();

			if (dbType1.toLowerCase().equals("mysql"))
				dbType1 = SQLServers.MySQL.toString();
			else
				dbType1 = SQLServers.Oracle.toString();


			configOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("<configuration>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("<source>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<publicIP>" + host + "</publicIP>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<port>" + port + "</port>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<database>" + chosenDatabase + "</database>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<table>" + chosenTable + "</table>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<user>" + user + "</user>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<password>" + password + "</password>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<sqlType>" + sqlType + "</sqlType>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("</source>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);

			configOut.write("<targets>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("<target>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<publicIP>" + publicIp1 + "</publicIP>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<port>" + port1 + "</port>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<database>" + dbName1 + "</database>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<table>" + partitionName1 + "</table>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<user>" + dbUser1 + "</user>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<password>" + dbPassword1 + "</password>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<sqlType>" + dbType1 + "</sqlType>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<partition>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			//configOut.write(SeDiCoConstants.TABULATOR);
			if (sb != null) {
				List<TableColHeader> partition1 = sb
						.getTableColHeaderListPartition1();
				Iterator<TableColHeader> it = partition1.iterator();

				while (it.hasNext()) {
					configOut.write(SeDiCoConstants.TABULATOR);
					configOut.write("<column>"
							+ it.next().getLabel() + "</column>"
							+ SeDiCoConstants.LINE_SEPARATOR);

				}
			}
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("</partition>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("</target>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);

			/*
			// Create file
			FileWriter fstream = new FileWriter(path);

			// log.info("Begin writing data to file: " + path);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("=====================Amazon-CloudData Partition 1 Begin==============="
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("PublicIP: " + publicIp1 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("Port: " + port1 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Name: " + dbName1 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("DB-Partition: " + partitionName1
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-User: " + dbUser1 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Password: " + dbPassword1
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Type: " + dbType1 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("PartitionData: " + SeDiCoConstants.LINE_SEPARATOR);
			if (sb != null) {
				List<TableColHeader> partition1 = sb
						.getTableColHeaderListPartition1();
				Iterator<TableColHeader> it = partition1.iterator();

				while (it.hasNext()) {
					out.write(SeDiCoConstants.TABULATOR + it.next().getLabel()
							+ SeDiCoConstants.LINE_SEPARATOR);

				}
			}

			out.write("=====================Amazon-CloudData Partition 1 End================="
					+ SeDiCoConstants.LINE_SEPARATOR);
			// Close the output stream
			 */
			log.info("Data written");
			configOut.close();
			fileConfigStream.close();
			//out.close();
			
			

		} catch (Exception e) {
			log.info("Error: " + e.getMessage());
		}
		
		inactivateFieldsGUI1 = true;
		
		return null;

	}

	public String resetValues() {

		return null;
	}
/**
 * Diese Methode lädt die Instanzdaten der zweiten Partition.
 * @return null
 */
	public String loadInstanceData2() {
		FacesContext fc = FacesContext.getCurrentInstance();
		log.info("Entering loadInstanceData2");

		String imageId = selectedImageEca2;
		log.info(imageId);
		// log.info(selectedImageAma1.getId());

		Iterator<NodeMetadata> i = myNodesEuca.iterator();
		while (i.hasNext()) {
			NodeMetadata m = i.next();
			if (m.getId().equals(selectedImageEca2)) {

				String publicIPList = m.getPublicAddresses().toString();
				publicIPList = publicIPList.replace("[", "");
				publicIPList = publicIPList.replace("]", "");
				publicIPList = publicIPList.trim();

				publicIp2 = publicIPList;

				break;

			}
			log.info("Meta-Data ID: " + m.getId());
		}



		TreeBean sb = TreeBean.getCurrentInstance();

		HttpServletResponse response = (HttpServletResponse) fc
				.getExternalContext().getResponse();

		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();
		String rootPath = ctx.getRealPath("/");

		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();
/*
		String path = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName()
				+ "/amazonPartition1Data.txt";
		File file = new File(path);
*/
		String sedicoConfigPath = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName() + "/sedico.config";
		File sedicoConfig = new File(sedicoConfigPath);

		
		
		//file.getParentFile().mkdirs();

		// use these variables to write the values into Patrick's config file
		// like in the code below
		try {

			FileWriter fileConfigStream = null;
			
			
			if (sedicoConfig.exists()) {  // append to file 
				fileConfigStream = new FileWriter(sedicoConfigPath, true); 
	            log.info("Dieser Text steht am Ende der bereits existierenden Datei."); 
	            
	        } else {  // create new file 
	        	fileConfigStream = new FileWriter(sedicoConfigPath); 
	            log.info("Dieser Text steht in der neuen Datei."); 
	            
	        } 
			BufferedWriter configOut = new BufferedWriter(fileConfigStream);
			
			// write the sedico.config file
			

			// FacesUtil.lookupManagedBeanName("treeBean");
			TreeBean tb = TreeBean.getCurrentInstance();

			String user = tb.getUser();
			String password = tb.getPassword();
			String host = tb.getHost();
			int port = tb.getPort();
			String chosenTable = tb.getChosenTable();
			String chosenDatabase = tb.getChosenDatabase();
			String sqlType = tb.getSqlType();

			if (sqlType.toLowerCase().equals("mysql"))
				sqlType = SQLServers.MySQL.toString();
			else
				sqlType = SQLServers.Oracle.toString();

			if (dbType2.toLowerCase().equals("mysql"))
				dbType2 = SQLServers.MySQL.toString();
			else
				dbType2 = SQLServers.Oracle.toString();

			configOut.write("<target>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<publicIP>" + publicIp2 + "</publicIP>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<port>" + port2 + "</port>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<database>" + dbName2 + "</database>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<table>" + partitionName2 + "</table>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<user>" + dbUser2 + "</user>");
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<password>" + dbPassword2 + "</password>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<sqlType>" + dbType2 + "</sqlType>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<partition>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			//configOut.write(SeDiCoConstants.TABULATOR);
			if (sb != null) {
				List<TableColHeader> partition2 = sb
						.getTableColHeaderListPartition2();
				Iterator<TableColHeader> it = partition2.iterator();

				while (it.hasNext()) {
					configOut.write(SeDiCoConstants.TABULATOR);
					configOut.write("<column>"
							+ it.next().getLabel() + "</column>"
							+ SeDiCoConstants.LINE_SEPARATOR);

				}
			}
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("</partition>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("</target>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("</targets>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("</configuration>");

			log.info("Data written");
			
			configOut.close();
			fileConfigStream.close();

		} catch (Exception e) {
			log.info("Error: " + e.getMessage());
		}

		inactivateFieldsGUI2 = true;
		return null;
		
		
		
		

	}
/**
 * Mit dieser Methode kann man die Auswahl der Datenbank der ersten Partition ändern.
 * @param e - AjaxBehaviorEvent
 */
	public void changeDatabaseSelection1(AjaxBehaviorEvent e) {
		dbType1 = ((UIOutput) e.getSource()).getValue().toString();
		log.info("dbType1 changed" + ((UIOutput) e.getSource()).getValue());

	}
/**
 * Mit dieser Methode kann man die Auswahl der Datenbank der zweiten Partition ändern.
 * @param e - AjaxBehaviorEvent
 */
	public void changeDatabaseSelection2(AjaxBehaviorEvent e) {
		dbType2 = ((UIOutput) e.getSource()).getValue().toString();
		log.info("dbType2 changed" + ((UIOutput) e.getSource()).getValue());

	}
/**
 * Diese Methode ändert die gewählte virtuelle Maschine der Partition 1.
 * @param e - AjaxBehaviorEvent
 */
	public void changeVMSelection1(AjaxBehaviorEvent e) {
		log.info("Entering changeVMSelection1");

		selectedEca1ImageId = ((UIOutput) e.getSource()).getValue().toString();
		log.info("selectedAma1ImageId changed"
				+ ((UIOutput) e.getSource()).getValue());

		if (selectedEca1ImageId != null) {
			String imageId = selectedEca1ImageId;
			log.info(imageId);
			// log.info(selectedImageAma1.getId());

			Iterator<NodeMetadata> i = myNodesEuca.iterator();
			while (i.hasNext()) {
				NodeMetadata m = i.next();
				if (m.getId().equals(selectedEca1ImageId)) {

					String publicIPList = m.getPublicAddresses().toString();
					publicIPList = publicIPList.replace("[", "");
					publicIPList = publicIPList.replace("]", "");
					publicIPList = publicIPList.trim();

					publicIp1 = publicIPList;
					log.info("New Public IP: " + publicIp1);
					break;

				}
				log.info("Meta-Data ID: " + m.getId());

			}
		} else {
			publicIp1 = "";
		}
		// getting the IP address from the selected image in chooseClouds.xhtml

	}
	/**
	 * Diese Methode ändert die gewählte virtuelle Maschine der Partition 2.
	 * @param e - AjaxBehaviorEvent
	 */
	public void changeVMSelection2(AjaxBehaviorEvent e) {
		log.info("Entering changeVMSelection2");

		selectedEca2ImageId = ((UIOutput) e.getSource()).getValue().toString();
		log.info("selectedEca2ImageId changed"
				+ ((UIOutput) e.getSource()).getValue());

		if (selectedEca2ImageId != null) {
			String imageId = selectedEca2ImageId;
			log.info(imageId);
			// log.info(selectedImageAma1.getId());

			Iterator<NodeMetadata> i = myNodesEuca.iterator();
			while (i.hasNext()) {
				NodeMetadata m = i.next();
				if (m.getId().equals(selectedEca2ImageId)) {

					String publicIPList = m.getPublicAddresses().toString();
					publicIPList = publicIPList.replace("[", "");
					publicIPList = publicIPList.replace("]", "");
					publicIPList = publicIPList.trim();

					publicIp2 = publicIPList;
					log.info("New Public IP: " + publicIp2);
					break;

				}
				log.info("Meta-Data ID: " + m.getId());

			}
		} else {
			publicIp2 = "";
		}
		// getting the IP address from the selected image in chooseClouds.xhtml

	}
/**
 * Diese Methode führt die Befehle der virtuellen Maschine aus. 
 * @return null
 */
	public String useVMsAction() {
		FacesContext fc = FacesContext.getCurrentInstance();

		Map<String, String> params = fc.getExternalContext()
				.getRequestParameterMap();

		log.info("publicIp1: " + publicIp1);
		log.info("port1: " + port1);
		log.info("dbName1: " + dbName1);
		log.info("partitionName1: " + partitionName1);
		log.info("dbUser1: " + dbUser1);
		log.info("dbPassword1: " + dbPassword1);
		log.info("DBType: " + dbType1);

		// getting the table headers of the first partition from
		// chooseClouds.xhtml
		log.info("getting TreeBean from Context");
		TreeBean sb = TreeBean.getCurrentInstance();

		log.info("Partition1 Size: "
				+ sb.getTableColHeaderListPartition1().size());

		HttpServletResponse response = (HttpServletResponse) fc
				.getExternalContext().getResponse();

		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();
		String rootPath = ctx.getRealPath("/");

		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();

		String path = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName()
				+ "/eucaPartition1Data.txt";
		File file = new File(path);

		String sedicoConfigPath = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName() + "/sedico.config";
		File sedicoConfig = new File(sedicoConfigPath);

		file.getParentFile().mkdirs();

		// use these variables to write the values into Patrick's config file
		// like in the code below
		try {

			// write the sedico.config file
			FileWriter fileConfigStream = new FileWriter(sedicoConfigPath);
			BufferedWriter configOut = new BufferedWriter(fileConfigStream);

			// FacesUtil.lookupManagedBeanName("treeBean");
			TreeBean tb = TreeBean.getCurrentInstance();

			String user = tb.getUser();
			String password = tb.getPassword();
			String host = tb.getHost();
			int port = tb.getPort();
			String chosenTable = tb.getChosenTable();
			String chosenDatabase = tb.getChosenDatabase();
			String sqlType = tb.getSqlType();

			if (sqlType.toLowerCase().equals("mysql"))
				sqlType = SQLServers.MySQL.toString();
			else
				sqlType = SQLServers.Oracle.toString();

			if (dbType1.toLowerCase().equals("mysql"))
				dbType1 = SQLServers.MySQL.toString();
			else
				dbType1 = SQLServers.Oracle.toString();

			if (dbType2.toLowerCase().equals("mysql"))
				dbType2 = SQLServers.MySQL.toString();
			else
				dbType2 = SQLServers.Oracle.toString();

			configOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("<configuration>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("<source>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<publicIP>" + host + "</publicIP>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<port>" + port + "</port>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<database>" + chosenDatabase + "</database>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<table>" + chosenTable + "</table>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<user>" + user + "</user>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<password>" + password + "</password>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<sqlType>" + sqlType + "</sqlType>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("</source>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);

			configOut.write("<targets>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("<target>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<publicIP>" + publicIp1 + "</publicIP>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<port>" + port1 + "</port>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<database>" + dbName1 + "</database>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<table>" + partitionName1 + "</table>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<user>" + dbUser1 + "</user>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<password>" + dbPassword1 + "</password>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<sqlType>" + dbType1 + "</sqlType>");
			configOut.write("<partition>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			if (sb != null) {
				List<TableColHeader> partition1 = sb
						.getTableColHeaderListPartition1();
				Iterator<TableColHeader> it = partition1.iterator();

				while (it.hasNext()) {
					configOut.write(SeDiCoConstants.TABULATOR + "<column>"
							+ it.next().getLabel() + "</column>"
							+ SeDiCoConstants.LINE_SEPARATOR);

				}
			}
			configOut.write("</partition>");
			configOut.write("</target>");

			configOut.write("<target>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<publicIP>" + publicIp2 + "</publicIP>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<port>" + port2 + "</port>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<database>" + dbName2 + "</database>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<table>" + partitionName2 + "</table>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<user>" + dbUser2 + "</user>");
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<password>" + dbPassword2 + "</password>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			configOut.write("<sqlType>" + dbType2 + "</sqlType>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write("<partition>");
			configOut.write(SeDiCoConstants.LINE_SEPARATOR);
			configOut.write(SeDiCoConstants.TABULATOR);
			if (sb != null) {
				List<TableColHeader> partition2 = sb
						.getTableColHeaderListPartition2();
				Iterator<TableColHeader> it = partition2.iterator();

				while (it.hasNext()) {
					configOut.write(SeDiCoConstants.TABULATOR + "<column>"
							+ it.next().getLabel() + "</column>"
							+ SeDiCoConstants.LINE_SEPARATOR);

				}
			}
			configOut.write("</partition>");

			configOut.write("</target>");

			configOut.write("</targets>");
			configOut.write("</configuration>");

			// Create file
			FileWriter fstream = new FileWriter(path);

			// log.info("Begin writing data to file: " + path);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("=====================Euca-CloudData Partition 1 Begin==============="
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("PublicIP: " + publicIp1 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("Port: " + port1 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Name: " + dbName1 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("DB-Partition: " + partitionName1
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-User: " + dbUser1 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Password: " + dbPassword1
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Type: " + dbType1 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("PartitionData: " + SeDiCoConstants.LINE_SEPARATOR);
			if (sb != null) {
				List<TableColHeader> partition1 = sb
						.getTableColHeaderListPartition1();
				Iterator<TableColHeader> it = partition1.iterator();

				while (it.hasNext()) {
					out.write(SeDiCoConstants.TABULATOR + it.next().getLabel()
							+ SeDiCoConstants.LINE_SEPARATOR);

				}
			}

			out.write("=====================Euca-CloudData Partition 1 End================="
					+ SeDiCoConstants.LINE_SEPARATOR);
			// Close the output stream

			log.info("Data written");
			configOut.close();
			out.close();

		} catch (Exception e) {
			log.info("Error: " + e.getMessage());
		}

		file.getParentFile().mkdirs();

		// use these variables to write the values into Patrick's config file
		// like in the code below
		try {
			// Create file
			FileWriter fstream = new FileWriter(path);
			log.info("Begin writing data to file: " + path);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("=====================Euca-CloudData Partition 2 Begin==============="
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("PublicIP: " + publicIp2 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("Port: " + port2 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Name: " + dbName2 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("DB-Partition: " + partitionName2
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-User: " + dbUser2 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Password: " + dbPassword2
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Type: " + dbType2 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("PartitionData: " + SeDiCoConstants.LINE_SEPARATOR);
			if (sb != null) {
				List<TableColHeader> partition2 = sb
						.getTableColHeaderListPartition2();
				Iterator<TableColHeader> it = partition2.iterator();

				while (it.hasNext()) {
					out.write(SeDiCoConstants.TABULATOR + it.next().getLabel()
							+ SeDiCoConstants.LINE_SEPARATOR);

				}
			}

			out.write("=====================Euca-CloudData Partition 2 End================="
					+ SeDiCoConstants.LINE_SEPARATOR);

			log.info("Data written");
			out.close();
		} catch (Exception e) {
			log.info("Error: " + e.getMessage());
		}

		de.sedico.sql.importing.SQLImportStrategy strategy = new SQLImportStrategyBase();
		Table table = strategy.fetchTable();
		de.sedico.partition.TableWriter writer = new de.sedico.partition.TableWriter();

		System.out.println("Zieltabellen werden erstellt.");
		writer.createTable(table);

		System.out.println("Daten werden in Zieltabellen geschrieben");
		writer.insert(table);

		return null;

	}
/**
 * Diese Methode führt die Befehle der virtuellen Maschine der ersten Partition aus.
 * @return null
 */
	public String useVMAction1() {
		log.info("Entering EUCA useVM1");
		FacesContext fc = FacesContext.getCurrentInstance();

		Map<String, String> params = fc.getExternalContext()
				.getRequestParameterMap();

		// getting the field information for partition1 from inputfields in
		// chooseClouds.xhtml
		/*
		 * log.info("No of params: " +params.size()); for (Map.Entry<String,
		 * String> entry : params.entrySet()) { log.info("Key = " +
		 * entry.getKey() + ", Value = " + entry.getValue()); }
		 */
		log.info("Request-publicIp1: " + params.get("publicIp1"));
		log.info("Request-port1: " + params.get("port1"));
		log.info("Request-dbName1: " + params.get("dbName1"));
		log.info("Request-partitionName1: " + params.get("partitionName1"));
		log.info("Request-dbuser: " + params.get("dbUser1"));
		log.info("Request-DBPassword: " + params.get("dbPassword1"));

		publicIp1 = params.get("publicIp1");
		port1 = Integer.parseInt(params.get("port1"));
		dbName1 = params.get("dbName1");
		partitionName1 = params.get("partitionName1");
		dbUser1 = params.get("dbUser1");
		dbPassword1 = params.get("dbPassword1");

		// dbType1 is set via changeListener: public void
		// changeDatabaseSelection1(AjaxBehaviorEvent e)
		// dbType1 = params.get("dbType1");

		// use thise variables to write the values into Patrick's config file

		log.info("publicIp1: " + publicIp1);
		log.info("port1: " + port1);
		log.info("dbName1: " + dbName1);
		log.info("partitionName1: " + partitionName1);
		log.info("dbUser1: " + dbUser1);
		log.info("dbPassword1: " + dbPassword1);
		log.info("DBType: " + dbType1);

		// getting the table headers of the first partition from
		// chooseClouds.xhtml
		log.info("getting TreeBean from Context");
		TreeBean sb = TreeBean.getCurrentInstance();

		log.info("Partition2 Size: "
				+ sb.getTableColHeaderListPartition1().size());

		if (sb != null) {
			List<TableColHeader> partition1 = sb
					.getTableColHeaderListPartition1();
			Iterator<TableColHeader> it = partition1.iterator();

			while (it.hasNext()) {
				log.info("Partition2: Label: " + it.next().getLabel());

			}
		}

		HttpServletResponse response = (HttpServletResponse) fc
				.getExternalContext().getResponse();

		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();
		String rootPath = ctx.getRealPath("/");

		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();

		String path = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName()
				+ "/eucalyptusPartition1Data.txt";
		File file = new File(path);
		file.getParentFile().mkdirs();

		try {
			// Create file
			FileWriter fstream = new FileWriter(path);

			log.info("Begin writing data to file: " + path);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("=====================Amazon-CloudData Begin==============="
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("PublicIP: " + publicIp1 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("Port: " + port1 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Name: " + dbName1 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("DB-Partition: " + partitionName1
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-User: " + dbUser1 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Password: " + dbPassword1
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Type: " + dbType1 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("PartitionData: " + SeDiCoConstants.LINE_SEPARATOR);
			if (sb != null) {
				List<TableColHeader> partition1 = sb
						.getTableColHeaderListPartition1();
				Iterator<TableColHeader> it = partition1.iterator();

				while (it.hasNext()) {
					out.write(SeDiCoConstants.TABULATOR + it.next().getLabel()
							+ SeDiCoConstants.LINE_SEPARATOR);

				}
			}

			out.write("=====================Amazon-CloudData End================="
					+ SeDiCoConstants.LINE_SEPARATOR);

			// Close the output stream

			log.info("Data written");
			out.close();
		} catch (Exception e) {
			log.info("Error: " + e.getMessage());
		}

		SQLServers typ;

		if (dbType1.toLowerCase().equals("mysql"))
			typ = SQLServers.MySQL;
		else
			typ = SQLServers.Oracle;
		/*
		 * Configuration.addTargetConfiguration("euca1", publicIp1, port1,
		 * dbName1, partitionName1, dbUser1, dbPassword1, typ,
		 * sb.getTableColHeaderListPartition1());
		 */
		return null;
	}
/**
 * Diese Methode führt die Befehle der virtuellen Maschine der ersten Partition aus.
 * @return null
 */
	public String useVMAction2() {
		log.info("Entering EUCA useVM2");
		FacesContext fc = FacesContext.getCurrentInstance();

		Map<String, String> params = fc.getExternalContext()
				.getRequestParameterMap();

		// getting the field information for partition1 from inputfields in
		// chooseClouds.xhtml
		/*
		 * log.info("No of params: " +params.size()); for (Map.Entry<String,
		 * String> entry : params.entrySet()) { log.info("Key = " +
		 * entry.getKey() + ", Value = " + entry.getValue()); }
		 */
		log.info("Request-publicIp2: " + params.get("publicIp2"));
		log.info("Request-port2: " + params.get("port2"));
		log.info("Request-dbName2: " + params.get("dbName2"));
		log.info("Request-partitionName2: " + params.get("partitionName2"));
		log.info("Request-dbuser2: " + params.get("dbUser2"));
		log.info("Request-DBPassword2: " + params.get("dbPassword2"));

		publicIp2 = params.get("publicIp2");
		port2 = Integer.parseInt(params.get("port2"));
		dbName2 = params.get("dbName2");
		partitionName2 = params.get("partitionName2");
		dbUser2 = params.get("dbUser2");
		dbPassword2 = params.get("dbPassword2");

		// dbType1 is set via changeListener: public void
		// changeDatabaseSelection1(AjaxBehaviorEvent e)
		// dbType1 = params.get("dbType1");

		// use thise variables to write the values into Patrick's config file

		log.info("publicIp2: " + publicIp2);
		log.info("port2: " + port2);
		log.info("dbName2: " + dbName2);
		log.info("partitionName2: " + partitionName2);
		log.info("dbUser2: " + dbUser2);
		log.info("dbPassword2: " + dbPassword2);
		log.info("DBType2: " + dbType2);

		// getting the table headers of the first partition from
		// chooseClouds.xhtml
		log.info("getting TreeBean from Context");
		TreeBean sb = TreeBean.getCurrentInstance();

		log.info("Partition2 Size: "
				+ sb.getTableColHeaderListPartition2().size());

		if (sb != null) {
			List<TableColHeader> partition2 = sb
					.getTableColHeaderListPartition2();
			Iterator<TableColHeader> it = partition2.iterator();

			while (it.hasNext()) {
				log.info("Partition2: Label: " + it.next().getLabel());

			}
		}

		HttpServletResponse response = (HttpServletResponse) fc
				.getExternalContext().getResponse();

		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();
		String rootPath = ctx.getRealPath("/");

		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();

		String path = rootPath + "WEB-INF/zipPackage/"
				+ request.getUserPrincipal().getName()
				+ "/eucalyptusPartition2Data.txt";
		File file = new File(path);
		file.getParentFile().mkdirs();

		try {
			// Create file
			FileWriter fstream = new FileWriter(path);

			log.info("Begin writing data to file: " + path);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("=====================Amazon-CloudData Begin==============="
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("PublicIP: " + publicIp2 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("Port: " + port2 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Name: " + dbName2 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("DB-Partition: " + partitionName2
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-User: " + dbUser2 + SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Password: " + dbPassword2
					+ SeDiCoConstants.LINE_SEPARATOR);
			out.write("DB-Type: " + dbType2 + SeDiCoConstants.LINE_SEPARATOR);

			out.write("PartitionData: " + SeDiCoConstants.LINE_SEPARATOR);
			if (sb != null) {
				List<TableColHeader> partition2 = sb
						.getTableColHeaderListPartition2();
				Iterator<TableColHeader> it = partition2.iterator();

				while (it.hasNext()) {
					out.write(SeDiCoConstants.TABULATOR + it.next().getLabel()
							+ SeDiCoConstants.LINE_SEPARATOR);

				}
			}

			out.write("=====================Amazon-CloudData End================="
					+ SeDiCoConstants.LINE_SEPARATOR);

			// Close the output stream

			log.info("Data written");
			out.close();
		} catch (Exception e) {
			log.info("Error: " + e.getMessage());
		}

		SQLServers typ;

		if (dbType2.toLowerCase().equals("mysql"))
			typ = SQLServers.MySQL;
		else
			typ = SQLServers.Oracle;
		/*
		 * Configuration.addTargetConfiguration("euca2", publicIp2, port2,
		 * dbName2, partitionName2, dbUser2, dbPassword2, typ,
		 * sb.getTableColHeaderListPartition2());
		 */
		return null;
	}
	/**
	 * Diese Methode filtert die Ressourcen der ersten Partition.
	 * @return null
	 */
	public String filterResources1() {

		String os = euca1os;
		int ram = euca1ram;
		int procs = euca1procs;
		int hd = euca1harddisk;

		for (int i = myNodesEuca.size() - 1; i >= 0; i--) {
			NodeMetadata node = myNodesEuca.get(i);

			log.info(node.getHardware().getRam());
			log.info(node.getHardware().getProcessors().size());
			log.info(node.getHardware().getVolumes().size());

			if (node.getOperatingSystem().getFamily().toString()
					.equalsIgnoreCase(os)
					&& node.getHardware().getRam() == ram
					&& node.getHardware().getProcessors().size() == procs
					&& node.getHardware().getVolumes().size() == hd) {
				log.info("True");

			} else
				myNodesEuca.remove(i);
		}

		return null;
	}
	/**
	 * Diese Methode filtert die Ressourcen der zweiten Partition.
	 * @return null
	 */
	public String filterResources2() {

		String os = euca2os;
		int ram = euca2ram;
		int procs = euca2procs;
		int hd = euca2harddisk;

		for (int i = myNodesEuca.size() - 1; i >= 0; i--) {
			NodeMetadata node = myNodesEuca.get(i);

			log.info(node.getHardware().getRam());
			log.info(node.getHardware().getProcessors().size());
			log.info(node.getHardware().getVolumes().size());

			if (node.getOperatingSystem().getFamily().toString()
					.equalsIgnoreCase(os)
					&& node.getHardware().getRam() == ram
					&& node.getHardware().getProcessors().size() == procs
					&& node.getHardware().getVolumes().size() == hd) {
				log.info("True");

			} else
				myNodesEuca.remove(i);
		}

		return null;
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

	public int getFoundImages() {
		return foundImages;
	}

	public void setFoundImages(int foundImages) {
		this.foundImages = foundImages;
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

	public String getSelectedDatabase() {
		return selectedDatabase;
	}

	public void setSelectedDatabase(String selectedDatabase) {
		this.selectedDatabase = selectedDatabase;
	}

	public List<String> getDatabase() {
		return database;
	}

	public void setDatabase(List<String> database) {
		this.database = database;
	}

	public boolean isShowCloudTab() {
		return showCloudTab;
	}

	public void setShowCloudTab(boolean showCloudTab) {
		this.showCloudTab = showCloudTab;
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

	public String getSelectedImageEca1() {
		return selectedImageEca1;
	}

	public void setSelectedImageEca1(String selectedImageEca1) {
		this.selectedImageEca1 = selectedImageEca1;
	}

	public String getSelectedImageEca2() {
		return selectedImageEca2;
	}

	public void setSelectedImageEca2(String selectedImageEca2) {
		this.selectedImageEca2 = selectedImageEca2;
	}

	public String getPublicIp1() {
		return publicIp1;
	}

	public void setPublicIp1(String publicIp1) {
		this.publicIp1 = publicIp1;
	}

	public int getPort1() {
		return port1;
	}

	public void setPort1(int port1) {
		this.port1 = port1;
	}

	public String getDbName1() {
		return dbName1;
	}

	public void setDbName1(String dbName1) {
		this.dbName1 = dbName1;
	}

	public String getPartitionName1() {
		return partitionName1;
	}

	public void setPartitionName1(String partitionName1) {
		this.partitionName1 = partitionName1;
	}

	public String getDbUser1() {
		return dbUser1;
	}

	public void setDbUser1(String dbUser1) {
		this.dbUser1 = dbUser1;
	}

	public String getDbPassword1() {
		return dbPassword1;
	}

	public void setDbPassword1(String dbPassword1) {
		this.dbPassword1 = dbPassword1;
	}

	public String getDbType1() {
		return dbType1;
	}

	public void setDbType1(String dbType1) {
		this.dbType1 = dbType1;
	}

	public String getPublicIp2() {
		return publicIp2;
	}

	public void setPublicIp2(String publicIp2) {
		this.publicIp2 = publicIp2;
	}

	public int getPort2() {
		return port2;
	}

	public void setPort2(int port2) {
		this.port2 = port2;
	}

	public String getDbName2() {
		return dbName2;
	}

	public void setDbName2(String dbName2) {
		this.dbName2 = dbName2;
	}

	public String getPartitionName2() {
		return partitionName2;
	}

	public void setPartitionName2(String partitionName2) {
		this.partitionName2 = partitionName2;
	}

	public String getDbUser2() {
		return dbUser2;
	}

	public void setDbUser2(String dbUser2) {
		this.dbUser2 = dbUser2;
	}

	public String getDbPassword2() {
		return dbPassword2;
	}

	public void setDbPassword2(String dbPassword2) {
		this.dbPassword2 = dbPassword2;
	}

	public String getDbType2() {
		return dbType2;
	}

	public void setDbType2(String dbType2) {
		this.dbType2 = dbType2;
	}

}
