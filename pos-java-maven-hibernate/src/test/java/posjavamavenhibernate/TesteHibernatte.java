package posjavamavenhibernate;

import java.util.List;

import org.junit.Test;

import DAO.DaoGenerico;
import model.TelefoneUser;
import model.UsuarioPessoa;

public class TesteHibernatte {

	@Test
	public void testeHibernateUtil() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();

		UsuarioPessoa pessoa = new UsuarioPessoa();
		pessoa.setIdade(45);
		pessoa.setLogin("teste");
		pessoa.setNome("Paulo");
		pessoa.setSenha("123");
		pessoa.setSobrenome("Martins");
		pessoa.setEmail("marcusvini370@gmail.com");

		daoGenerico.salvar(pessoa);

	}

	@Test
	public void testeBuscar() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		UsuarioPessoa pessoa = new UsuarioPessoa();
		pessoa.setId(1L);

		pessoa = daoGenerico.pesquisar(pessoa);

		System.out.print(pessoa);

	}

	@Test
	public void testeBuscar2() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();

		UsuarioPessoa pessoa = daoGeneric.pesquisar(5L, UsuarioPessoa.class);

		System.out.println(pessoa);
	}

	@Test
	public void testeUpdate() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();

		UsuarioPessoa pessoa = daoGeneric.pesquisar(8L, UsuarioPessoa.class);
		pessoa.setIdade(99);
		pessoa.setNome("Leoro");

		pessoa = daoGeneric.updateMerge(pessoa);

		System.out.println(pessoa);

	}

	@Test
	public void testeDelete() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();

		UsuarioPessoa pessoa = daoGeneric.pesquisar(3L, UsuarioPessoa.class);

		daoGeneric.deletarPorId(pessoa);

	}

	@Test
	public void testeConsultar() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();

		List<UsuarioPessoa> list = daoGeneric.listar(UsuarioPessoa.class);

		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
			System.out.println(
					"----------------------------------------------------------------------------------------------------------------");
		}
	}

	@Test
	public void testeQueryList() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();
		List<UsuarioPessoa> list = daoGeneric.getEntityManager()
				.createQuery("from UsuarioPessoa where nome = 'Vinicius' ").getResultList();

		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}

	@Test
	public void testeQueryListMaxResult() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();
		List<UsuarioPessoa> list = daoGeneric.getEntityManager().createQuery("from UsuarioPessoa order by id ")
				.setMaxResults(4).getResultList();

		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}

	@Test
	public void testeQueryListParameter() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();

		List<UsuarioPessoa> list = daoGeneric.getEntityManager()
				.createQuery("from UsuarioPessoa where nome = :nome or sobrenome = :sobrenome")
				.setParameter("nome", "Vinicius").setParameter("sobrenome", "Martins").getResultList();

		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}

	}

	@Test
	public void testeQuerySomaIdade() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();

		Long somaIdade = (Long) daoGeneric.getEntityManager().createQuery("select sum(u.idade) from UsuarioPessoa u ")
				.getSingleResult();
		System.out.println("Soma de todas as idades é  --> " + somaIdade);

	}

	@Test
	public void testeNameQuery1() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();
		List<UsuarioPessoa> list = daoGeneric.getEntityManager().createNamedQuery("UsuarioPessoas.todos")
				.getResultList();

		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}

	@Test
	public void testeNameQuery2() {
		DaoGenerico<UsuarioPessoa> daoGeneric = new DaoGenerico<UsuarioPessoa>();
		List<UsuarioPessoa> list = daoGeneric.getEntityManager().createNamedQuery("UsuarioPessoas.buscaPorNome")
				.setParameter("nome", "Vinicius")	
				.getResultList();

		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}

	@Test
	public void testeGravaTelefone() {
		DaoGenerico daoGeneric = new DaoGenerico();
		
		UsuarioPessoa pessoa = (UsuarioPessoa) daoGeneric.pesquisar(6L, UsuarioPessoa.class);
		
		TelefoneUser telefoneUser = new TelefoneUser();
		
		telefoneUser.setTipo("Celular");
		telefoneUser.setNumero("6163637783");
		telefoneUser.setUsuarioPessoa(pessoa);
		
		daoGeneric.salvar(telefoneUser);
	}
	
	@Test
	public void testeConsultarTelefone() {
		
		 DaoGenerico daoGeneric = new DaoGenerico ();

		 UsuarioPessoa pessoa = (UsuarioPessoa) daoGeneric.pesquisar(6L, UsuarioPessoa.class);
 		  
		 for (TelefoneUser fone : pessoa.getTelefoneUsers()) {
			
			 //lista dos usuarios pessoas
		 System.out.println("Nome User: " + fone.getUsuarioPessoa().getNome() + " Tipo: " + fone.getTipo() + " Número: " + fone.getNumero());
			 System.out.println("--------------------------------------------------------");
			
		}
	}
	
}
