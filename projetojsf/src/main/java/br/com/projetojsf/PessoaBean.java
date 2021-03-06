package br.com.projetojsf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.google.gson.Gson;

import br.com.dao.DAOGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImpl;


@ViewScoped
@ManagedBean(name = "PessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	private DAOGeneric<Pessoa> daoGeneric = new DAOGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();
	
	private List<SelectItem> estados;
	
	private List<SelectItem> cidades;
	
	private Part arquivofoto;

	public String salvar() {
		
		System.out.println(arquivofoto);
		
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		mostrarMsg("Cadastrado com Sucesso!");
		return "";
	}

	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
		
	}

	public String novo() {
		/* Executa algum processo antes do novor */
		pessoa = new Pessoa();
		return "";
	}
	
	public String LIMPAR() {
		/* Executa algum processo antes de limpar */
		pessoa = new Pessoa();
		return "";
	}

	public String remove() {
		daoGeneric.deletePorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		mostrarMsg("Removido com Sucesso!");
		return "";
	}

	
	@PostConstruct
	public void carregarPessoas() {
		pessoas = daoGeneric.getListEntity(Pessoa.class);
	}

	public Pessoa getPessoa() {
		return pessoa;
	}
	
	public void pesquisaCep(AjaxBehaviorEvent event) {
	
		try {
			
			URL url = new URL("https://viacep.com.br/ws/"+ pessoa.getCep() +"/json/");
			
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			String cep = "";
			StringBuilder JsonCep = new StringBuilder(); 
			
			while((cep = br.readLine()) != null){
				JsonCep.append(cep);
			}
			
			Pessoa gsonAux = new Gson().fromJson(JsonCep.toString(), Pessoa.class);
			
			pessoa.setCep(gsonAux.getCep());
			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setComplemento(gsonAux.getComplemento());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade());
			pessoa.setUf(gsonAux.getUf());
		    pessoa.setIbge(gsonAux.getIbge());
		    
		    
		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("Erro ao consultar o cep");
		}
		
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DAOGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DAOGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	
	
	public String deslogar() {
		
		//Remover o usu??rio da sess??o usuarioLogado
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) context.getCurrentInstance().getExternalContext().getRequest();
		
		httpServletRequest.getSession().invalidate();
		
		return "index.jsf";
	}
	
	public String logar() {
		
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(),pessoa.getSenha());
		
		if(pessoa != null) { //achou o usu??rio
			
			//adicionar o usu??rio na sess??o usuarioLogado
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser);
			
			
			return "primeirapagina.jsf";
			
		}
		
		return"index.jsf";
	}
	
	
	public boolean permiteAcesso(String acesso) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		
		return pessoaUser.getPerfilUser().equals(acesso);
	}
	
	public List<SelectItem> getEstados(){
		estados = iDaoPessoa.listaEstados();
		return estados;
	}
	
	public void carregaCidades(AjaxBehaviorEvent event) {

		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();

			
			if(estado != null) {
				pessoa.setEstados(estado);
				
				List<Cidades> cidades = JPAUtil
						.getEntityManager()
						.createQuery("from Cidades where estados.id = " + estado.getId()).getResultList();
				
				List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
				
				for (Cidades cidade : cidades) {
					selectItemsCidade.add(new SelectItem(cidade, cidade.getNome()));
				}
				
				setCidades(selectItemsCidade);
				 
			}
			
	}
	
	public void editar() {
		if(pessoa.getCidades() != null) {
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);

			List<Cidades> cidades = JPAUtil
					.getEntityManager()
					.createQuery("from Cidades where estados.id = " + estado.getId()).getResultList();
			
			List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
			
			for (Cidades cidade : cidades) {
				selectItemsCidade.add(new SelectItem(cidade, cidade.getNome()));
			}
			
			setCidades(selectItemsCidade);
			 
		}
			
		}
	
	
		public void setCidades(List<SelectItem> cidades) {
			this.cidades = cidades;
		}
			public List<SelectItem> getCidades() {
				return cidades;
			}

			public Part getArquivofoto() {
				return arquivofoto;
			}

			public void setArquivofoto(Part arquivofoto) {
				this.arquivofoto = arquivofoto;
			}

			public void setEstados(List<SelectItem> estados) {
				this.estados = estados;
			}

/* M??todo que converte inputStream para array de bytes */
private byte[] getByte (InputStream is) throws IOException {
	
	int len;
	int size =1024;
	byte[] buf = null;
	if(is instanceof ByteArrayInputStream) {
		size = is.available();
		buf = new byte[size];
		len = is.read(buf, 0, size);
	}else {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		buf = new byte[size];
		
		while((len = is.read(buf, 0, size)) != -1){
			bos.write(buf, 0, len);
		}
		buf = bos.toByteArray();
	}
	return buf;
}
}
