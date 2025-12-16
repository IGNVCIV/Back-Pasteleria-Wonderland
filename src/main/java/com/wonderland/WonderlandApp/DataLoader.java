package com.wonderland.WonderlandApp;

import net.datafaker.Faker;
import com.wonderland.WonderlandApp.model.*;
import com.wonderland.WonderlandApp.repository.*;
import com.wonderland.WonderlandApp.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.*;
import java.util.*;

@Configuration
public class DataLoader implements CommandLineRunner {

    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private RequestRepository requestRepository;
    @Autowired private MessageContactRepository messageContactRepository;
    //@Autowired private RequestDetailRepository requestDetailRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Faker faker = new Faker(new Locale("es", "CL"));
    private Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0)
                loadProducts();

        if (userRepository.count() == 0)
                loadUsers();

        if (employeeRepository.count() == 0)
                loadEmployees();

        List<Request> pedidos = null;
        if (requestRepository.count() == 0) {
                pedidos = loadRequests();              
                List<Products> productos = productRepository.findAll();
                loadRequestDetailsConCascade(pedidos, productos);
        }
        if (messageContactRepository.count() == 0)
                loadMessages();
        System.out.println("✔ DataLoader ejecutado correctamente.");
    }


    private List<Products> loadProducts() {

        List<Products> catalogo = List.of(
                new Products("TC001", "Torta Cuadrada de Chocolate",
                                "Bizcocho de chocolate intenso elaborado con cacao puro y mantequilla natural. Su textura húmeda y su sabor profundo la hacen ideal para los amantes del chocolate.",
                                45000, "Tortas Cuadradas", 120,
                                "assets/img/Catalogo/tortas-cuadradas/cuadrada-chocolate.jpg"),

                new Products("TC002", "Torta Cuadrada de Frutas",
                                "Torta esponjosa con trozos de frutas naturales, equilibrada en dulzura y aroma fresco.",
                                50000, "Tortas Cuadradas", 80,
                                "assets/img/Catalogo/tortas-cuadradas/cuadrada-frutas.jpg"),

                new Products("TT001", "Torta Circular de Vainilla",
                                "Torta circular de vainilla preparada con ingredientes frescos y extracto natural de vainilla.",
                                40000, "Tortas Circulares", 450,
                                "assets/img/Catalogo/tortas-circulares/circular-vainilla.jpg"),

                new Products("TT002", "Torta Circular de Manjar",
                                "Torta artesanal rellena con manjar y cubierta con un glaseado dorado.",
                                42000, "Tortas Circulares", 95,
                                "assets/img/Catalogo/tortas-circulares/circular-manjar.jpeg"),

                new Products("PI001", "Mousse de Chocolate",
                                "Delicioso mousse con textura ligera y sabor profundo, elaborado con cacao 70%.",
                                5000, "Postres Individuales", 180,
                                "assets/img/Catalogo/postres-individuales/mousse-chocolate.jpg"),

                new Products("PI002", "Tiramisú Clásico",
                                "Receta tradicional italiana con capas de bizcocho empapado en café y crema de mascarpone.",
                                5500, "Postres Individuales", 210,
                                "assets/img/Catalogo/postres-individuales/tiramisu.jpg"),

                new Products("PSA001", "Torta Sin Azúcar de Naranja",
                                "Bizcocho húmedo endulzado con stevia, preparado con jugo natural de naranja.",
                                48000, "Productos Sin Azúcar", 60,
                                "assets/img/Catalogo/sin-azúcar/torta-naranja.jpg"),

                new Products("PSA002", "Cheesecake Sin Azúcar",
                                "Cheesecake sin azúcar refinada, endulzado naturalmente.",
                                47000, "Productos Sin Azúcar", 85,
                                "assets/img/Catalogo/sin-azúcar/cheesecake.jpg"),

                new Products("PT001", "Empanada de Manzana",
                                "Receta tradicional con trozos de manzana caramelizada y canela.",
                                3000, "Pastelería Tradicional", 160,
                                "assets/img/Catalogo/tradicional/empanada-manzana.jpg"),

                new Products("PT002", "Tarta de Santiago",
                                "La auténtica Tarta de Santiago elaborada con harina de almendra, sin gluten y con sabor a tradición española.",
                                6000, "Pastelería Tradicional", 70,
                                "assets/img/Catalogo/tradicional/tarta-santiago.jpg"),

                new Products("PG001", "Brownie Sin Gluten",
                                "Textura húmeda y sabor profundo, hecho con harina de almendras.",
                                4000, "Productos Sin Gluten", 130,
                                "assets/img/Catalogo/sin-gluten/brownie.webp"),

                new Products("PG002", "Pan Sin Gluten",
                                "Elaborado con mezcla de harinas naturales sin trigo.",
                                3500, "Productos Sin Gluten", 100,
                                "assets/img/Catalogo/sin-gluten/pan.webp"),

                new Products("PV001", "Torta Vegana de Chocolate",
                                "Elaborada con cacao puro y aceite vegetal, textura húmeda y sabor intenso.",
                                50000, "Productos Veganos", 300,
                                "assets/img/Catalogo/vegano/torta-chocolate.jpg"),

                new Products("PV002", "Galletas Veganas de Avena",
                                "Crujientes por fuera y suaves por dentro. Opción nutritiva sin ingredientes animales.",
                                4500, "Productos Veganos", 140,
                                "assets/img/Catalogo/vegano/galletas-avena.jpg"),

                new Products("TE001", "Torta Especial de Cumpleaños",
                                "Personalizable con mensajes o decoraciones. Bizcocho suave y cobertura cremosa.",
                                55000, "Tortas Especiales", 200,
                                "assets/img/Catalogo/tortas-especiales/torta-cumpleanos.jpg"),

                new Products("TE002", "Torta Especial de Boda",
                                "Hecha por encargo con diseños personalizados y presentación premium.",
                                60000, "Tortas Especiales", 110,
                                "assets/img/Catalogo/tortas-especiales/torta-boda.webp"));
        catalogo.forEach(productRepository::save);
        return catalogo;
    }

    private User loadAdminUser() {

        User admin = new User();
        admin.setEmail("admin@wonderland.cl");
        admin.setPassword(passwordEncoder.encode("clave123"));
        admin.setFirstName("Elizabeth");
        admin.setLastName("Taylor");
        admin.setPhone("+56 9 1111 1111");
        admin.setRole("ADMIN");
        admin.setCreatedAt(LocalDateTime.now());

        return userRepository.save(admin);
    }

    private List<User> loadUsers() {

        List<User> users = new ArrayList<>();

        // Admin
        User admin = loadAdminUser();
        users.add(admin);

        // Employees users
        for (int i = 0; i < 10; i++) {

            User u = new User();
            u.setFirstName(faker.name().firstName());
            u.setLastName(faker.name().lastName());
            u.setEmail((u.getFirstName() + "." + u.getLastName() + "@mail.cl").toLowerCase());
            u.setPassword(passwordEncoder.encode("empleado123"));
            u.setPhone("+56 9 " + faker.number().digits(8));
            u.setRole("EMPLOYEE");
            u.setCreatedAt(LocalDateTime.now());

            users.add(u);
        }

        return userRepository.saveAll(users);
    }

    private void loadEmployees() {

        VerificarRut rutValidator = new VerificarRut();
        Set<Integer> rutUsed = new HashSet<>();
        String[] positions = {
                "Pastelero", "Panadero", "Cajero", "Repostero",
                "Mesero", "Encargado", "Vendedor"
        };

        List<User> users = userRepository.findAll();
        List<Employee> employees = new ArrayList<>();

        // Admin Employee
        User adminUser = users.stream().filter(u -> u.getRole().equals("ADMIN")).findFirst().orElse(null);

        Employee admin = new Employee();
        admin.setRut(23927139);
        admin.setDv("1");
        admin.setFirstName("Elizabeth");
        admin.setMiddleName("Ophelia");
        admin.setLastName("Taylor");
        admin.setSecondLastName("Swift");
        admin.setBirthDate(LocalDate.now().minusYears(35));
        admin.setPosition("Administrador General");
        admin.setUser(adminUser);

        employees.add(admin);

        // Regular Employees
        for (User u : users) {
            if (u.getRole().equals("ADMIN"))
                continue;

            int rut;
            String rutFull;
            do {
                rut = faker.number().numberBetween(10_000_000, 28_000_000);
                String dv = CalculadorDv.obtenerDigitoVerificador(rut);
                rutFull = rut + "-" + dv;
            } while (rutUsed.contains(rut) || !rutValidator.esRutValido(rutFull));

            rutUsed.add(rut);

            Employee e = new Employee();
            e.setRut(rut);
            e.setDv(CalculadorDv.obtenerDigitoVerificador(rut));
            e.setFirstName(u.getFirstName());
            e.setMiddleName(faker.name().firstName());
            e.setLastName(u.getLastName());
            e.setSecondLastName(faker.name().lastName());
            e.setBirthDate(LocalDate.now().minusYears(faker.number().numberBetween(18, 60)));
            e.setPosition(positions[random.nextInt(positions.length)]);
            e.setUser(u);

            employees.add(e);
        }

        employeeRepository.saveAll(employees);
    }

    private List<Request> loadRequests() {

        List<Request> list = new ArrayList<>();
        LocalDateTime baseDate = LocalDateTime.of(2025, 10, 3, 8, 0);
        String[] estados = { "PENDIENTE", "EN_PROCESO", "COMPLETADO", "CANCELADO" };

        for (int i = 0; i < 13; i++) {

            Request r = new Request();
            r.setOrderDate(baseDate.plusDays(faker.number().numberBetween(0, 120)));
            r.setStatus(faker.options().option(estados));
            r.setTotal(0);
            r.setUser(null);
            r.setCustomerName(faker.name().fullName());
            r.setCustomerEmail(faker.internet().emailAddress());
            r.setCustomerPhone("+56 9 " + faker.number().digits(8));
            r.setRequestDetails(new ArrayList<>());
            r.setMessageContacts(new ArrayList<>());

            list.add(r);
        }

        return requestRepository.saveAll(list);
    }

    private void loadRequestDetailsConCascade(List<Request> pedidos, List<Products> productos) {

        for (Request pedido : pedidos) {

                int totalPedido = 0;
                int cantidadProductos = faker.number().numberBetween(1, 4);

                for (int i = 0; i < cantidadProductos; i++) {

                    Products producto = productos.get(
                                faker.number().numberBetween(0, productos.size())
                    );

                    int cantidad = faker.number().numberBetween(1, 4);
                    RequestDetail detalle = new RequestDetail();
                    detalle.setProducts(producto);
                    detalle.setQuantity(cantidad);
                    detalle.setUnitPrice(producto.getPrice());
                    detalle.setRequest(pedido);
                    pedido.getRequestDetails().add(detalle);

                    totalPedido += producto.getPrice() * cantidad;
                }

                pedido.setTotal(totalPedido);
        }

        requestRepository.saveAll(pedidos);
    }

    private void loadMessages() {

        List<Request> requests = requestRepository.findAll();
        List<MessageContact> mensajes = new ArrayList<>();

        String[] estadosMsg = { "PENDIENTE", "EN_PROCESO", "RESUELTO" };

        // Mensajes con pedido
        for (int i = 0; i < Math.min(4, requests.size()); i++) {

            Request req = requests.get(i);

            MessageContact m = new MessageContact();
            String nombreGenerado1 = faker.name().fullName();
            m.setFullName(nombreGenerado1);
            m.setEmail(faker.internet().emailAddress(nombreGenerado1));
            m.setPhone("+56 9 " + faker.number().digits(8));
            m.setMessage("Consulta sobre el pedido #" + req.getIdRequest());
            m.setSentDate(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 50)));
            m.setStatus(faker.options().option(estadosMsg));
            m.setRequest(req);

            mensajes.add(m);
        }

        // Mensajes sin pedido
        for (int i = 0; i < 4; i++) {

            MessageContact m = new MessageContact();
            String nombreGenerado2 = faker.name().fullName();
            m.setFullName(nombreGenerado2);
            m.setEmail(faker.internet().emailAddress(nombreGenerado2));
            m.setPhone("+56 9 " + faker.number().digits(8));
            m.setMessage("Consulta general de contacto.");
            m.setSentDate(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 50)));
            m.setStatus(faker.options().option(estadosMsg));
            m.setRequest(null);

            mensajes.add(m);
        }

        messageContactRepository.saveAll(mensajes);
    }

}
