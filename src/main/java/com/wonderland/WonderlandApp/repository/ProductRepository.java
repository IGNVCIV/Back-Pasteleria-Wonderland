package com.wonderland.WonderlandApp.repository;

//Importacion Clase Model
import com.wonderland.WonderlandApp.model.Products;

//Importaciones para BD con SpringData JPA
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

//Importacion para funcionamiento de repository
import org.springframework.stereotype.Repository;

//Importacion de Java
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Products, String> {

    List<Products> findByName(String name);

    @Query("SELECT p FROM Products  p WHERE p.name = :nam")
    List<Products> buscarPorJNombre(@Param("nam") String name);

    List<Products> findByCategory(String category);

    boolean existsById(@NonNull String id_products);    

    int countByCategory(String category);

    List<Products> findAllByOrderByPriceAsc();//Para mostrar catalogo ordenado en el react

}
