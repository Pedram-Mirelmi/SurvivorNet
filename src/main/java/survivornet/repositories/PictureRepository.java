package survivornet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import survivornet.models.Picture;

public interface PictureRepository extends JpaRepository<Picture, Long> {

}
