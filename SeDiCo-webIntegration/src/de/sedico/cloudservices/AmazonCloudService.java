package de.sedico.cloudservices;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.cms.MetaData;
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
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.sshj.config.SshjSshClientModule;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import de.sedico.partition.PartitionByColumnNamesStrategy;
import de.sedico.partition.PartitionerStrategy;
import de.sedico.partition.SQLTableStrategy;
import de.sedico.sql.MySqlConnectionDescriptor;

import de.sedico.sql.Column;
import de.sedico.sql.ColumnDescriptor;
import de.sedico.sql.MySQLBuilder;
import de.sedico.sql.PrimaryColumnDescriptor;
import de.sedico.sql.Row;
import de.sedico.sql.SQLBuilder;
import de.sedico.sql.Table;

import de.sedico.generictableadapter.TableColHeader;
import de.sedico.generictableadapter.TreeBean;
import de.sedico.interfaces.ICloudService;
import de.sedico.partition.Configuration;
import de.sedico.partition.PartitionDescriptor;
import de.sedico.partition.SQLServers;
import de.sedico.sql.SqlConnectionDescriptor;
import de.sedico.sql.importing.SQLImportStrategyBase;
import de.sedico.sql.writing.MySQLWriterStrategy;
import de.sedico.sql.writing.SQLWriterStrategyBase;
import de.sedico.tools.FacesUtil;
import de.sedico.tools.SeDiCoConstants;
import de.sedico.tools.SeDiCoFileWriter;

@ManagedBean(name = "amazonCloudService")
@SessionScoped

/**
 * Diese Klasse implementiert den Cloud-Service der Amazon-Cloud. Sie implementiert das interface Serializable, ICloudService.
 * @author jens
 *
 */
public class AmazonCloudService implements Serializable, ICloudService {
	private static Logger log = Logger.getLogger(AmazonCloudService.class);
	private static final long serialVersionUID = 1L;
	private String amaAccessKey;
	private String amaUserKey;

	private String divideSuccessMessage;
	
	public String getDivideSuccessMessage() {
		return divideSuccessMessage;
	}

	public void setDivideSuccessMessage(String divideSuccessMessage) {
		this.divideSuccessMessage = divideSuccessMessage;
	}

	List<NodeMetadata> myNodesAma;
	List<Image> myImagesAma;
	List<Template> myTemplatesAma;
	NodeMetadata metadata;

	public NodeMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(NodeMetadata metadata) {
		this.metadata = metadata;
	}

	private String selectedTemplateAma1;
	private NodeMetadata selectedNodeAma1;
	private Image selectedImageAma1;
	private String selectedTemplateAma2;
	private NodeMetadata selectedNodeAma2;
	private Image selectedImageAma2;
	private String selectedAma1ImageId;

	public String getSelectedAma1ImageId() {
		return selectedAma1ImageId;
	}

	public void setSelectedAma1ImageId(String selectedAma1ImageId) {
		this.selectedAma1ImageId = selectedAma1ImageId;
	}

	private String selectedAma2ImageId;

	public String getSelectedAma2ImageId() {
		return selectedAma2ImageId;
	}

	public void setSelectedAma2ImageId(String selectedAma2ImageId) {
		this.selectedAma2ImageId = selectedAma2ImageId;
	}

	private int foundImages;

	private String amazon1os;
	private int amazon1ram;
	private int amazon1procs;
	private int amazon1harddisk;

	private String amazon2os;
	private int amazon2ram;
	private int amazon2procs;
	private int amazon2harddisk;

	private String selectedDatabase;
	private boolean showCloudTab = false;

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

	private boolean inactivateFieldsGUI1 = false;
	private boolean inactivateFieldsGUI2 = false;

	public AmazonCloudService() {

	}

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

	FacesContext context;
	/**
	 * Diese Methode loggt einen Nutzer in die Cloud ein.
	 * @throws AuthorizationException - falls falsche Daten eingegeben werden
	 * @throws NoSuchElementException - falls Benutzer nicht gefunden wird
	 * @return null
	 */
	@Override
	public String cloudLogin() throws AuthorizationException,
			NoSuchElementException {
		try {
			findCloudResources();
			setShowCloudTab(true);

			return null;
		}

		catch (AuthorizationException ae) {
			FacesMessage facesMessage = new FacesMessage("Wrong Credentials");
			FacesContext.getCurrentInstance().addMessage("ama_wrongCreds",
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
	 * Diese Methode sucht die Ressourcen der Cloud.
	 * @throws NoSuchElementException - falls, keine Informationen gefunden werden
	 * @return null
	 */
	@Override
	public String findCloudResources() throws NoSuchElementException {
		ComputeService computeService = getComputeServiceContext(true);
		myNodesAma = new ArrayList<NodeMetadata>();
		myImagesAma = new ArrayList<Image>();
		myTemplatesAma = new ArrayList<Template>();

		// code to find instances
		for (ComputeMetadata nodes : computeService.listNodes()) {
			metadata = computeService.getNodeMetadata(nodes.getId());

			myNodesAma.add(metadata);

		}

		// code to find templates
		// Template template = null;
		/*
		 * switch (amazon1os) { case "UBUNTU" : template =
		 * computeService.templateBuilder
		 * ().osFamily(OsFamily.UBUNTU).minRam(amazon1ram
		 * ).minCores(amazon1procs).minDisk(amazon1harddisk).build(); break;
		 * 
		 * case "WINDOWS": template =
		 * computeService.templateBuilder().osFamily(OsFamily
		 * .WINDOWS).minRam(amazon1ram
		 * ).minCores(amazon1procs).minDisk(amazon1harddisk).build(); break;
		 * 
		 * case "DEBIAN": template =
		 * computeService.templateBuilder().osFamily(OsFamily
		 * .DEBIAN).minRam(amazon1ram
		 * ).minCores(amazon1procs).minDisk(amazon1harddisk).build(); break;
		 * 
		 * case "CENTOS": template =
		 * computeService.templateBuilder().osFamily(OsFamily
		 * .CENTOS).minRam(amazon1ram
		 * ).minCores(amazon1procs).minDisk(amazon1harddisk).build(); break;
		 * 
		 * default: template =
		 * computeService.templateBuilder().osFamily(OsFamily
		 * .UBUNTU).minRam(amazon1ram
		 * ).minCores(amazon1procs).minDisk(amazon1harddisk).build(); }
		 * 
		 * 
		 * Hardware hardware = template.getHardware();
		 * System.out.println("===============TEMPLATE DATA======================="
		 * ); System.out.println("ID: " + hardware.getId());
		 * System.out.println("Hypervisor: " + hardware.getHypervisor());
		 * System.out.println("Name: " + hardware.getName());
		 * System.out.println("RAM: " + hardware.getRam());
		 * 
		 * //System.out.println("Location: " +
		 * hardware.getLocation().toString()); ComputeType[] ct =
		 * hardware.getType().values(); for (int j=0; j<ct.length; j++) {
		 * System.out.println("Type-Name: " +ct[j].name()); } Iterator<? extends
		 * Processor> processorIterator = hardware.getProcessors().iterator();
		 * while(processorIterator.hasNext()) { Processor p =
		 * processorIterator.next(); System.out.println("Processor Cores: "
		 * +p.toString()); } myTemplatesAma.add(template);
		 */
		computeService.getContext().close();

		// code to find images (experimental)
		/*
		 * Set<? extends Image> imageList = computeService.listImages(); for
		 * (Image img : imageList) { myImagesAma.add(img);
		 * 
		 * }
		 * 
		 * foundImages = imageList.size();
		 */
		return null;
	}
	/**
	 * Diese Methode erzeugt den Context auf dem Bildschirm
	 * @param logginOn - prüft, ob der Nutzer eingeloggt ist
	 * @return context.buildView(ComputeServiceContext.class)
				.getComputeService() - liefert den Text auf den Bildschirm
	 */
	@Override
	public ComputeService getComputeServiceContext(boolean logginOn) {
		Iterable<Module> modules;
		Properties properties = new Properties();
		// properties.setProperty("jclouds.regions", "aws-ec2");
		// properties.setProperty(PROPERTY_EC2_URL,
		// "http://ec2.amazonaws.com/");

		if (logginOn == true) {
			modules = ImmutableSet.<Module> of(new SshjSshClientModule(),
					new SLF4JLoggingModule());
		} else {
			modules = ImmutableSet.<Module> of(new SshjSshClientModule());
		}

		ContextBuilder context = ContextBuilder
				.newBuilder("aws-ec2")
				// .credentials(amaAccessKey, amaUserKey)
				.credentials(amaAccessKey, amaUserKey)
				// .endpoint("ec2.us-west-2.amazonaws.com")
				.modules(modules);
		// .overrides(properties);

		System.out.printf(">> initializing %s%n", context.getApiMetadata());

		return context.buildView(ComputeServiceContext.class)
				.getComputeService();

	}
	/**
	 * Diese Methode lädt die Instanzdateien der ersten Partition.
	 * @return null
	 */
	public String loadInstanceData1() {
		log.info("Entering loadInstanceData1");

		// getting the IP address from the selected image in chooseClouds.xhtml
		String imageId = selectedAma1ImageId;
		log.info(imageId);
		// log.info(selectedImageAma1.getId());

		Iterator<NodeMetadata> i = myNodesAma.iterator();
		while (i.hasNext()) {
			NodeMetadata m = i.next();
			if (m.getId().equals(selectedAma1ImageId)) {

				String publicIPList = m.getPublicAddresses().toString();
				publicIPList = publicIPList.replace("[", "");
				publicIPList = publicIPList.replace("]", "");
				publicIPList = publicIPList.trim();

				publicIp1 = publicIPList;

				break;

			}
			log.info("Meta-Data ID: " + m.getId());
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
	 * Mit dieser Methode kann man die Auswahl der Datenbank der ersten Partition ändern.
	 * @param e - AjaxBehaviorEvent
     */
	public void changeDatabaseSelection1(AjaxBehaviorEvent e) {
		dbType1 = ((UIOutput) e.getSource()).getValue().toString();
		log.info("dbType1 changed" + ((UIOutput) e.getSource()).getValue());

	}
	/**
	 * Mit dieser Methode kann man die Datenbank ändern, welche am als zweites der Auswahl gewählt hat.
	 * @param e - AjaxBehaviorEvent
	 */
	public void changeDatabaseSelection2(AjaxBehaviorEvent e) {
		dbType2 = ((UIOutput) e.getSource()).getValue().toString();
		log.info("dbType2 changed" + ((UIOutput) e.getSource()).getValue());

	}
	/**
	 * Mit dieser Methode kann man die Auswahl der virtuellen Maschine der ersten Partition ändern
	 * @param e - AjaxBehaviorEvent
	 */
	public void changeVMSelection1(AjaxBehaviorEvent e) {
		log.info("Entering changeVMSelection1");

		selectedAma1ImageId = ((UIOutput) e.getSource()).getValue().toString();
		log.info("selectedAma1ImageId changed"
				+ ((UIOutput) e.getSource()).getValue());

		if (selectedAma1ImageId != null) {
			String imageId = selectedAma1ImageId;
			log.info(imageId);
			// log.info(selectedImageAma1.getId());

			Iterator<NodeMetadata> i = myNodesAma.iterator();
			while (i.hasNext()) {
				NodeMetadata m = i.next();
				if (m.getId().equals(selectedAma1ImageId)) {

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
	 * Mit dieser Methode kann man die Auswahl der virtuellen Maschine der zweiten Partition ändern.
	 * @param e - AjaxBehaviorEvent
	 */
	public void changeVMSelection2(AjaxBehaviorEvent e) {
		log.info("Entering changeVMSelection2");

		selectedAma2ImageId = ((UIOutput) e.getSource()).getValue().toString();
		log.info("selectedAma2ImageId changed"
				+ ((UIOutput) e.getSource()).getValue());

		if (selectedAma2ImageId != null) {
			String imageId = selectedAma2ImageId;
			log.info(imageId);
			// log.info(selectedImageAma2.getId());

			Iterator<NodeMetadata> i = myNodesAma.iterator();
			while (i.hasNext()) {
				NodeMetadata m = i.next();
				if (m.getId().equals(selectedAma2ImageId)) {

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
	 * Diese Methode sorgt für die Eingabe der Informationen in der virtuellen Maschine.
	 * @return null;
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
				+ "/amazonPartition1Data.txt";
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
			out.write("=====================Amazon-CloudData Partition 2 Begin==============="
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

			out.write("=====================Amazon-CloudData Partition 2 End================="
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
	 * Diese Methode benutzt die Aktionen der virtuellen Maschine der Partition 1
	 * @return null
	 */
	public String useVMAction1() {

		log.info("publicIp1: " + publicIp1);
		log.info("port1: " + port1);
		log.info("dbName1: " + dbName1);
		log.info("partitionName1: " + partitionName1);
		log.info("dbUser1: " + dbUser1);
		log.info("dbPassword1: " + dbPassword1);
		log.info("DBType: " + dbType1);
		log.info("Leaving useVMAction1");

		return null;
	}
	/**
	 * Diese Methode schreibt Daten in die Datenbank.
	 * @return null
	 */
	public String writeTODB() {
		de.sedico.sql.importing.SQLImportStrategy strategy = new SQLImportStrategyBase();
		Table table = strategy.fetchTable();
		de.sedico.partition.TableWriter writer = new de.sedico.partition.TableWriter();

		System.out.println("Zieltabellen werden erstellt.");
		writer.createTable(table);

		System.out.println("Daten werden in Zieltabellen geschrieben");
		writer.insert(table);

		divideSuccessMessage = new String("Partitioned databases created and data inserted!");
		
		
		return null;
			
	}
	/**
	 * Diese Methode benutzt die Aktionen der virtuellen Maschine der zweiten Partition.
	 * @return null
	 */
	public String useVMAction2() {
		log.info("publicIp2: " + publicIp2);
		log.info("port2: " + port2);
		log.info("dbName2: " + dbName2);
		log.info("partitionName2: " + partitionName2);
		log.info("dbUser2: " + dbUser2);
		log.info("dbPassword2: " + dbPassword2);
		log.info("DBType2: " + dbType2);
		log.info("Leaving useVMAction2");

		/*
		 * FacesContext fc = FacesContext.getCurrentInstance();
		 * 
		 * Map<String, String> params = fc.getExternalContext()
		 * .getRequestParameterMap();
		 * 
		 * 
		 * 
		 * 
		 * // getting the field information for partition1 from inputfields in
		 * // chooseClouds.xhtml /* log.info("No of params: " +params.size());
		 * for (Map.Entry<String, String> entry : params.entrySet()) {
		 * log.info("Key = " + entry.getKey() + ", Value = " +
		 * entry.getValue()); }
		 */
		/*
		 * log.info("Request-publicIp2: " + params.get("publicAmaIp2"));
		 * log.info("Request-port2: " + params.get("port2"));
		 * log.info("Request-dbName2: " + params.get("dbName2"));
		 * log.info("Request-partitionName2: " + params.get("partitionName2"));
		 * log.info("Request-dbuser2: " + params.get("dbUser2"));
		 * log.info("Request-DBPassword2: " + params.get("dbPassword2"));
		 * 
		 * System.out.println(("Request-publicIp2: " +
		 * params.get("publicAmaIp2"))); System.out.println(("Request-port2: " +
		 * params.get("port2"))); System.out.println(("Request-dbName2: " +
		 * params.get("dbName2")));
		 * System.out.println(("Request-partitionName2: " +
		 * params.get("partitionName2")));
		 * System.out.println(("Request-dbuser2: " + params.get("dbUser2")));
		 * System.out.println(("Request-DBPassword2: " +
		 * params.get("dbPassword2")));
		 * 
		 * publicIp2 = params.get("publicIp2"); port2 =
		 * Integer.parseInt(params.get("port2")); dbName2 =
		 * params.get("dbName2"); partitionName2 = params.get("partitionName2");
		 * dbUser2 = params.get("dbUser2"); dbPassword2 =
		 * params.get("dbPassword2");
		 * 
		 * // dbType1 is set via changeListener: public void //
		 * changeDatabaseSelection1(AjaxBehaviorEvent e) // dbType1 =
		 * params.get("dbType1");
		 * 
		 * log.info("publicIp2: " + publicIp2); log.info("port2: " + port2);
		 * log.info("dbName2: " + dbName2); log.info("partitionName2: " +
		 * partitionName2); log.info("dbUser2: " + dbUser2);
		 * log.info("dbPassword2: " + dbPassword2); log.info("DBType2: " +
		 * dbType2);
		 * 
		 * // getting the table headers of the first partition from //
		 * chooseClouds.xhtml log.info("getting TreeBean from Context");
		 * TreeBean sb = TreeBean.getCurrentInstance();
		 * 
		 * log.info("Partition2 Size: " +
		 * sb.getTableColHeaderListPartition2().size());
		 * 
		 * /* if (sb != null) { List<TableColHeader> partition1 =
		 * sb.getTableColHeaderListPartition1(); Iterator<TableColHeader> it =
		 * partition1.iterator();
		 * 
		 * while (it.hasNext()) { log.info("Partition1: Label: "
		 * +it.next().getLabel());
		 * 
		 * } }
		 */
		/*
		 * HttpServletResponse response = (HttpServletResponse) fc
		 * .getExternalContext().getResponse();
		 * 
		 * ServletContext ctx = (ServletContext)
		 * FacesContext.getCurrentInstance() .getExternalContext().getContext();
		 * String rootPath = ctx.getRealPath("/");
		 * 
		 * HttpServletRequest request = (HttpServletRequest) fc
		 * .getExternalContext().getRequest();
		 * 
		 * String path = rootPath + "WEB-INF/zipPackage/" +
		 * request.getUserPrincipal().getName() + "/amazonPartition2Data.txt";
		 * File file = new File(path); file.getParentFile().mkdirs();
		 * 
		 * // use these variables to write the values into Patrick's config file
		 * // like in the code below try { // Create file FileWriter fstream =
		 * new FileWriter(path); log.info("Begin writing data to file: " +
		 * path); BufferedWriter out = new BufferedWriter(fstream); out.write(
		 * "=====================Amazon-CloudData Partition 2 Begin==============="
		 * + SeDiCoConstants.LINE_SEPARATOR); out.write("PublicIP: " + publicIp2
		 * + SeDiCoConstants.LINE_SEPARATOR); out.write("Port: " + port2 +
		 * SeDiCoConstants.LINE_SEPARATOR); out.write("DB-Name: " + dbName2 +
		 * SeDiCoConstants.LINE_SEPARATOR);
		 * 
		 * out.write("DB-Partition: " + partitionName2 +
		 * SeDiCoConstants.LINE_SEPARATOR); out.write("DB-User: " + dbUser2 +
		 * SeDiCoConstants.LINE_SEPARATOR); out.write("DB-Password: " +
		 * dbPassword2 + SeDiCoConstants.LINE_SEPARATOR); out.write("DB-Type: "
		 * + dbType2 + SeDiCoConstants.LINE_SEPARATOR);
		 * 
		 * out.write("PartitionData: " + SeDiCoConstants.LINE_SEPARATOR); if (sb
		 * != null) { List<TableColHeader> partition2 = sb
		 * .getTableColHeaderListPartition2(); Iterator<TableColHeader> it =
		 * partition2.iterator();
		 * 
		 * while (it.hasNext()) { out.write(SeDiCoConstants.TABULATOR +
		 * it.next().getLabel() + SeDiCoConstants.LINE_SEPARATOR);
		 * 
		 * } }
		 * 
		 * out.write(
		 * "=====================Amazon-CloudData Partition 2 End================="
		 * + SeDiCoConstants.LINE_SEPARATOR); // Close the output stream
		 * 
		 * log.info("Data written"); out.close(); } catch (Exception e) {
		 * log.info("Error: " + e.getMessage()); }
		 * 
		 * // config
		 * 
		 * SQLServers typ;
		 * 
		 * if (dbType2.toLowerCase().equals("mysql")) typ = SQLServers.MySQL;
		 * else typ = SQLServers.Oracle;
		 * 
		 * Configuration.addTargetConfiguration("amazon2", publicIp2, port2,
		 * dbName2, partitionName2, dbUser2, dbPassword2, typ,
		 * sb.getTableColHeaderListPartition2());
		 * 
		 * // Database writeInDB(publicIp2, port2, dbName2, partitionName2,
		 * dbUser2, dbPassword2, typ);
		 */
		return null;
	}
	/**
	 * Diese Methode filtert die Ressourcen der ersten Partition
	 * @return null
	 */
	public String filterResources1() {

		String os = amazon1os;
		int ram = amazon1ram;
		int procs = amazon1procs;
		int hd = amazon1harddisk;

		for (int i = myNodesAma.size() - 1; i >= 0; i--) {
			NodeMetadata node = myNodesAma.get(i);

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
				myNodesAma.remove(i);
		}

		return null;
	}
	/**
	 * Diese Methode filtert die Ressourcen der zweiten Partition.
	 * @return null
	 */
	public String filterResources2() {

		String os = amazon2os;
		int ram = amazon2ram;
		int procs = amazon2procs;
		int hd = amazon2harddisk;

		for (int i = myNodesAma.size() - 1; i >= 0; i--) {
			NodeMetadata node = myNodesAma.get(i);

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
				myNodesAma.remove(i);
		}

		return null;
	}
	/**
	 * Diese Methode lädt die Instanzdaten der zweiten Partition.
	 * @return null
	 */
	public String loadInstanceData2() {
		FacesContext fc = FacesContext.getCurrentInstance();
		log.info("Entering loadInstanceData2");

		String imageId = selectedAma2ImageId;
		log.info(imageId);
		// log.info(selectedImageAma1.getId());

		Iterator<NodeMetadata> i = myNodesAma.iterator();
		while (i.hasNext()) {
			NodeMetadata m = i.next();
			if (m.getId().equals(selectedAma2ImageId)) {

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

	public int getAmazon1ram() {
		return amazon1ram;
	}

	public void setAmazon1ram(int amazon1ram) {
		this.amazon1ram = amazon1ram;
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

	public String getSelectedDatabase() {
		return selectedDatabase;
	}

	public void setSelectedDatabase(String selectedDatabase) {
		this.selectedDatabase = selectedDatabase;
	}

	public boolean isShowCloudTab() {
		return showCloudTab;
	}

	public void setShowCloudTab(boolean showCloudTab) {
		this.showCloudTab = showCloudTab;
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
