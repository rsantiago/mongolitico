/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package cashback.mongolitico;

import org.junit.Test;

import cashback.mongolitico.data.MongoDataAcess;
import cashbackDomain.album.Album;
import cashbackDomain.album.Genre;
import cashbackDomain.customer.Customer;
import cashbackDomain.objects.identity.Id;
import cashbackDomain.promo.Promo;
import cashbackDomain.promo.PromoUtils;
import cashbackDomain.purchase.Item;
import cashbackDomain.purchase.Order;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class CashbackMongoTest {
    	@Test public void testMongoDataAcess() {
        MongoDataAcess mongo = new MongoDataAcess();
        // é necessário iniciar o serviço.
        
        mongo.init();
        //apaga todo o banco
        mongo.deleteEverything();
        
        assertTrue("Mongo está online e vazio", mongo!=null);
        
        Set<Id<Genre>> genreIds = mongo.getGenreIds();
        Set<Genre> genres = mongo.getGenres(genreIds);
        
        // o banco vai retornar conjuntos vazios
        assertNotNull("Pegou os ids dos gêneros", genreIds);
        assertNotNull("Pegou os gêneros", genres);
        assertTrue("A resposta está vazia para ambos", genreIds.size()==0);
        
        // construindo três novos gêneros para o banco
        Genre sertanejao = mongo.getGenreFactory().buildGenre("Sertanejao");
        Genre forro = mongo.getGenreFactory().buildGenre("Forró");
        Genre rock = mongo.getGenreFactory().buildGenre("Rock");
        

        // salvando no banco de dados
        Genre savedSertanejao = mongo.saveGenre(sertanejao);
        Genre savedForro = mongo.saveGenre(forro);
        Genre savedRock = mongo.saveGenre(rock);
        
        assertTrue("novo genero salvo retornou corretamente: ", sertanejao.equals(savedSertanejao));
        assertTrue("novo genero salvo retornou corretamente: ", forro.equals(savedForro));
        assertTrue("novo genero salvo retornou corretamente: ", rock.equals(savedRock));
        
        Genre readSertanejao = mongo.getGenre(savedSertanejao.getId());
        Genre readForro = mongo.getGenre(savedForro.getId());
        Genre readRock = mongo.getGenre(savedRock.getId());
        
        assertTrue("genero lido corretamente", readSertanejao.getId().equals(savedSertanejao.getId()));
        assertTrue("genero lido corretamente", readForro.getId().equals(savedForro.getId()));
        assertTrue("genero lido corretamente", readRock.getId().equals(savedRock.getId()));
        
        assertTrue("Generos sertanejo diferente de rock", readSertanejao.equals(readRock)==false);
        assertTrue("Generos inseridos são diferentes", readSertanejao.equals(readForro)==false);
        assertTrue("Generos inseridos são diferentes", readForro.equals(readRock)==false);
        
        // construindo novas promos para o banco
        
        Genre pop = mongo.getGenreFactory().buildGenre("Pop");
        Genre mpb = mongo.getGenreFactory().buildGenre("MPB");
        Genre classic = mongo.getGenreFactory().buildGenre("Classic");

        Set<Genre> genresToSave = new HashSet<Genre>();
        genresToSave.add(pop);
        genresToSave.add(mpb);
        genresToSave.add(classic);
        genresToSave.add(savedSertanejao);
        genresToSave.add(rock);
        genresToSave.add(forro);
        
        mongo.saveAllGenres(genresToSave);
        Set<Id<Genre>> allGenreIds = mongo.getGenreIds();
        Set<Genre> allGenres = mongo.getGenres(allGenreIds);
        
        assertTrue("são 6 generos no banco", allGenres.size()==6);
        
        Set<Promo> defaultPromos = PromoUtils.getDefaultPromos(pop, mpb, classic, readRock);

        mongo.saveAllPromos(defaultPromos);
        Set<Id<Promo>> promoIds = mongo.getPromoIds();
        assertTrue("28 promos padrões criadas corretamente", promoIds.size()==28);
        Set<Promo> promos = mongo.getPromos(promoIds);
        assertTrue("28 objetos de promoção lidos corretamente", promos.size()==28);
        
        // Cadastrar clientes
        Customer barack = mongo.getCustomerFactory().buildCustomer("Barack Obama");
        Customer zora = mongo.getCustomerFactory().buildCustomer("Zora Leonara");
        Customer caco = mongo.getCustomerFactory().buildCustomer("Caco Barcelos");
        Customer jabor = mongo.getCustomerFactory().buildCustomer("Arnaldo Jabor");
        
        Set<Customer> customers = new HashSet<Customer>();
        customers.add(barack);
        customers.add(zora);
        customers.add(caco);
        customers.add(jabor);
        customers.add(mongo.getCustomerFactory().buildCustomer("João Gonçalves"));
        customers.add(mongo.getCustomerFactory().buildCustomer("Fernanda Lima"));
        customers.add(mongo.getCustomerFactory().buildCustomer("Margaret Tatcher"));
        customers.add(mongo.getCustomerFactory().buildCustomer("Xuxa"));
        customers.add(mongo.getCustomerFactory().buildCustomer("Giselle Bunchen"));
        customers.add(mongo.getCustomerFactory().buildCustomer("Pablo Vittar"));
        
        mongo.saveAllCustomers(customers);
        Set<Id<Customer>> customerIds = mongo.getCustomerIds();
        assertTrue("São 10 clientes no banco", customerIds.size()==10);
        
        Set<Customer> readCustomers = mongo.getCustomers(customerIds);
        boolean sameCustomers = customers.equals(readCustomers);
        assertTrue("Os clientes lidos são idênticos aos inseridos no banco", sameCustomers);
        
        // Cadastrando albums
        
        Album nevermind = mongo.getAlbumFactory().buildAlbum("Nirvana", "Nevermind", rock.getId());
        Album henriqueJulianoAoVivo = mongo.getAlbumFactory().buildAlbum("Henrique e Juliano", "Ao Vivo H&J", sertanejao.getId());
        Album diabranco = mongo.getAlbumFactory().buildAlbum("Geraldo Azevedo", "Dia Branco", forro.getId());
        Album concertDiNapoli = mongo.getAlbumFactory().buildAlbum("Orquestra Italiana", "Concerto di Napoli", classic.getId());
        Album tiroCerto = mongo.getAlbumFactory().buildAlbum("Anitta", "Tiro Certo", pop.getId());
        Album beijoemMulher = mongo.getAlbumFactory().buildAlbum("Ana Carolina", "Beijo em Mulher", mpb.getId());
        
        Set<Album> albums = new HashSet<Album>();
        albums.add(nevermind);
        albums.add(henriqueJulianoAoVivo);
        albums.add(diabranco);
        albums.add(concertDiNapoli);
        albums.add(tiroCerto);
        albums.add(beijoemMulher);
        mongo.saveAllAlbums(albums);
        
        Set<Id<Album>> albumIds = mongo.getAlbumIds();
        assertTrue("Albums foram inseridos no banco", albumIds.size()==6);
        
        Set<Album> readAlbums = mongo.getAlbums(albumIds);
        assertTrue("Albums lidos são idênticos aos albums inseridos", albums.equals(readAlbums));
        
        // Teste de adição de pedidos
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        
        Order barackOrder = null, 
        		barackOrder2 = null, 
        		cacoOrder = null, 
        		zoraOrder = null, 
        		jaborOrder = null;
        
        try {
			barackOrder = mongo.getOrderFactory().buildOrder(barack, 
					dateformat.parse("2015-05-22"));
			cacoOrder = mongo.getOrderFactory().buildOrder(barack, 
					dateformat.parse("2015-02-11"));
			zoraOrder = mongo.getOrderFactory().buildOrder(barack, 
					dateformat.parse("2013-10-28"));
			jaborOrder = mongo.getOrderFactory().buildOrder(barack, 
					dateformat.parse("2017-04-23"));
			barackOrder2 = mongo.getOrderFactory().buildOrder(barack, 
					dateformat.parse("2018-08-30"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
        Item barackNevermind = mongo.getItemFactory().buildItem(nevermind, barackOrder, 1, mongo);
        
        Item barackAnitta = mongo.getItemFactory().buildItem(tiroCerto, barackOrder, 2);
        
        
        
        Set<Order> orders = new HashSet<Order>();
        orders.add(barackOrder);
        orders.add(barackOrder2);
        orders.add(zoraOrder);
        orders.add(jaborOrder);
        orders.add(cacoOrder);
        
        mongo.saveAllOrders(orders);
        
        
        
        // teste de delecao dos generos
        mongo.deleteAllGenres();
        Genre nullGenre = mongo.getGenre(readRock.getId());        
        assertNull("Não pode vir genero algum", nullGenre);
        
        // teste de delecao de customers
        mongo.deleteAllCustomers();
        Customer nullCustomer = mongo.getCustomer(barack.getId());        
        assertNull("Não pode vir cliente algum", nullCustomer);
        
        // teste de delecao de albums
        mongo.deleteAllAlbums();
        Album nullAlbum = mongo.getAlbum(concertDiNapoli.getId());        
        assertNull("Não pode vir cliente algum", nullAlbum);
        
        mongo.closeClient();
    }
}
