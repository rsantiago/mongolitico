package cashback.mongolitico.data;

import java.time.DayOfWeek;
import java.util.Set;
import com.mongodb.MongoClient;

import com.mongodb.client.MongoDatabase;

import cashback.mongolitico.data.stores.MongoAlbumStore;
import cashback.mongolitico.data.stores.MongoCustomerStore;
import cashback.mongolitico.data.stores.MongoGenreStore;
import cashback.mongolitico.data.stores.MongoItemStore;
import cashback.mongolitico.data.stores.MongoOrderStore;
import cashback.mongolitico.data.stores.MongoPromoStore;
import cashbackDomain.album.Album;
import cashbackDomain.album.Genre;
import cashbackDomain.customer.Customer;
import cashbackDomain.data.DataAccessInterface;
import cashbackDomain.factories.AlbumFactory;
import cashbackDomain.factories.CustomerFactory;
import cashbackDomain.factories.GenreFactory;
import cashbackDomain.factories.ItemFactory;
import cashbackDomain.factories.OrderFactory;
import cashbackDomain.factories.PromoFactory;
import cashbackDomain.objects.identity.Id;
import cashbackDomain.promo.Promo;
import cashbackDomain.purchase.Item;
import cashbackDomain.purchase.Order;
import cashbackDomain.setup.CashbackConfig;

public class MongoDataAcess implements DataAccessInterface {
	private MongoClient client = null;
	private MongoDatabase data = null;

	private MongoGenreStore genreStore;
	private MongoAlbumStore albumStore;
	private MongoCustomerStore customerStore;
	private MongoItemStore itemStore;
	private MongoOrderStore orderStore;
	private MongoPromoStore promoStore;

	private GenreFactory genreFactory;
	

	private AlbumFactory albumFactory;
	private ItemFactory itemFactory;
	private PromoFactory promoFactory;
	private OrderFactory orderFactory;
	private CustomerFactory customerFactory;

	public MongoDataAcess() {
	}

	public void init() {
		//this.client = new MongoClient("159.203.105.109", 27017);
		this.client = new MongoClient();
		this.data = client.getDatabase(CashbackConfig.DB_NAME);

		// configura as factories
		genreFactory = new GenreFactory();
		albumFactory = new AlbumFactory();
		itemFactory = new ItemFactory();
		promoFactory = new PromoFactory();
		orderFactory = new OrderFactory();
		customerFactory = new CustomerFactory();

		// configura os objetos com as conexões ao banco de dados
		genreStore = new MongoGenreStore(this,data.getCollection(Genre.COLLECTION_NAME), genreFactory);
		albumStore = new MongoAlbumStore(this,data.getCollection(Album.COLLECTION_NAME), albumFactory);
		customerStore = new MongoCustomerStore(this,data.getCollection(Customer.COLLECTION_NAME), customerFactory);
		itemStore = new MongoItemStore(this,data.getCollection(Item.COLLECTION_NAME), itemFactory);
		orderStore = new MongoOrderStore(this,data.getCollection(Order.COLLECTION_NAME), orderFactory);
		promoStore = new MongoPromoStore(this,data.getCollection(Promo.COLLECTION_NAME), promoFactory);
	}

	public static void main(String[] args) {
		MongoDataAcess mongo = new MongoDataAcess();
		mongo.init();
		
		System.out.println("conectou!" + mongo.toString());

	}

	@Override
	public Album getAlbum(Id<Album> album) {
		return albumStore.getById(album);
	}

	@Override
	public Set<Id<Album>> getAlbumIds() {
		return albumStore.getAllIds();
	}

	@Override
	public Set<Album> getAlbums(Set<Id<Album>> ids) {
		return albumStore.getByIds(ids);
	}

	@Override
	public Customer getCustomer(Id<Customer> id) {
		return customerStore.getById(id);
	}
	
	@Override
	public Set<Id<Customer>> getCustomerIds() {
		return customerStore.getAllIds();
	}

	@Override
	public Set<Customer> getCustomers(Set<Id<Customer>> ids) {
		return customerStore.getByIds(ids);
	}

	@Override
	public Genre getGenre(Id<Genre> id) {
		return genreStore.getById(id);
	}

	@Override
	public Set<Id<Genre>> getGenreIds() {
		return genreStore.getAllIds();
	}

	@Override
	public Set<Genre> getGenres(Set<Id<Genre>> ids) {
		return genreStore.getByIds(ids);
	}

	@Override
	public Item getItem(Id<Item> id) {
		return itemStore.getById(id);
	}

	@Override
	public Set<Id<Item>> getItemIds() {
		return itemStore.getAllIds();
	}

	@Override
	public Set<Item> getItems(Set<Id<Item>> ids) {
		return itemStore.getByIds(ids);
	}

	@Override
	public Order getOrder(Id<Order> id) {
		return orderStore.getById(id);
	}

	@Override
	public Set<Id<Order>> getOrderIds() {
		return orderStore.getAllIds();
	}

	@Override
	public Set<Order> getOrders(Set<Id<Order>> ids) {
		return orderStore.getByIds(ids);
	}

	@Override
	public Promo getPromo(Id<Promo> id) {
		return promoStore.getById(id);
	}

	@Override
	public Set<Id<Promo>> getPromoIds() {
		return promoStore.getAllIds();
	}

	@Override
	public Set<Promo> getPromos(Set<Id<Promo>> ids) {
		return promoStore.getByIds(ids);
	}

	
	
	@Override
	public Album saveAlbum(Album album) {
		return albumStore.save(album);
	}

	@Override
	public Customer saveCustomer(Customer customer) {
		return customerStore.save(customer);
	}

	@Override
	public Genre saveGenre(Genre genre) {
		return genreStore.save(genre);
	}

	@Override
	public Item saveItem(Item id) {
		return itemStore.save(id);
	}

	@Override
	public Order saveOrder(Order order) {
		return orderStore.save(order);
	}

	@Override
	public Promo savePromo(Promo promo) {
		return promoStore.save(promo);
	}

	public void closeClient() {
		client.close();
	}

	@Override
	public void deleteAllAlbums() {
		albumStore.deleteAllObjects();
	}

	@Override
	public void deleteAllCustomers() {
		customerStore.deleteAllObjects();
	}

	@Override
	public void deleteAllGenres() {
		genreStore.deleteAllObjects();
	}

	@Override
	public void deleteAllItems() {
		itemStore.deleteAllObjects();
	}

	@Override
	public void deleteAllOrders() {
		orderStore.deleteAllObjects();
	}

	@Override
	public void deleteAllPromos() {
		promoStore.deleteAllObjects();
	}

	@Override
	public void deleteEverything() {
		deleteAllPromos();
		deleteAllOrders();
		deleteAllItems();
		deleteAllGenres();
		deleteAllCustomers();
		deleteAllAlbums();
	}
	
	public GenreFactory getGenreFactory() {
		return genreFactory;
	}

	public AlbumFactory getAlbumFactory() {
		return albumFactory;
	}

	public ItemFactory getItemFactory() {
		return itemFactory;
	}

	public PromoFactory getPromoFactory() {
		return promoFactory;
	}

	public OrderFactory getOrderFactory() {
		return orderFactory;
	}

	public CustomerFactory getCustomerFactory() {
		return customerFactory;
	}

	@Override
	public void saveAllPromos(Set<Promo> promos) {
		for (Promo promo : promos) {
			promoStore.save(promo);	
		}
	}

	@Override
	public void saveAllGenres(Set<Genre> genres) {
		for (Genre genre : genres) {
			genreStore.save(genre);
		}
	}

	@Override
	public void saveAllCustomers(Set<Customer> customers) {
		for (Customer customer : customers) {
			customerStore.save(customer);
		}
	}

	@Override
	public void saveAllAlbums(Set<Album> albums) {
		for (Album album : albums) {
			albumStore.save(album);
		}
		
	}

	@Override
	public void saveAllItems(Set<Item> items) {
		for (Item item : items) {
			itemStore.save(item);
		}
	}

	@Override
	public void saveAllOrders(Set<Order> orders) {
		for (Order order : orders) {
			orderStore.save(order);
		}
	}

	@Override
	public Promo getPromoForDayOfWeek(DayOfWeek day, Id<Genre> genreId) {		
		return null;
	}
}
