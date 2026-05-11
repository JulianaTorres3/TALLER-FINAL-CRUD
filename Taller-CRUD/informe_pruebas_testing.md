# Informe de Casos de Prueba - Laboratorio 4

A continuación se detalla cada uno de los casos de prueba implementados para el laboratorio, junto con su respectivo código fuente (que puedes capturar directamente para los pantallazos de tu informe) y la leyenda explicativa.

---

## 1. Pruebas Unitarias: CreatureService (Mockito y JUnit 5)

### Caso: Creación de Criatura Exitosa
```java
@Test
void testCreateCreature_ShouldReturnSavedCreature() {
    Creature creature = new Creature();
    creature.setName("Fénix");
    creature.setSize(10.0);
    creature.setDangerLevel(5);

    when(creatureRepository.save(any(Creature.class))).thenReturn(creature);

    Creature savedCreature = creatureService.createCreature(creature);

    assertNotNull(savedCreature);
    assertEquals("Fénix", savedCreature.getName());
}
```
**Leyenda:** *Caso de prueba unitaria que valida la creación exitosa de una criatura simulando el guardado en el repositorio.*

### Caso: Creación con Tamaño Negativo (Error)
```java
@Test
void testCreateCreature_InvalidSize_ShouldThrowException() {
    Creature creature = new Creature();
    creature.setSize(-1.0);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        creatureService.createCreature(creature);
    });

    assertEquals("Size cannot be negative", exception.getMessage());
}
```
**Leyenda:** *Validación de los parámetros de entrada: El sistema rechaza y arroja una excepción si el tamaño de la criatura es negativo.*

### Caso: Creación con Nivel de Peligro Inválido (Error)
```java
@Test
void testCreateCreature_InvalidDangerLevel_ShouldThrowException() {
    Creature creature = new Creature();
    creature.setSize(10.0);
    creature.setDangerLevel(11);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        creatureService.createCreature(creature);
    });

    assertEquals("Danger level must be between 1 and 10", exception.getMessage());
}
```
**Leyenda:** *Validación de restricciones: Comprueba que el nivel de peligro debe estar estrictamente en el rango de 1 a 10.*

### Caso: Búsqueda Exitosa por ID
```java
@Test
void testGetCreatureById_Success() {
    Creature creature = new Creature();
    creature.setId(1L);
    when(creatureRepository.findById(1L)).thenReturn(Optional.of(creature));

    Creature found = creatureService.getCreatureById(1L);

    assertNotNull(found);
    assertEquals(1L, found.getId());
}
```
**Leyenda:** *Búsqueda exitosa simulada de una criatura existente utilizando su ID.*

### Caso: Búsqueda de ID No Encontrado
```java
@Test
void testGetCreatureById_NotFound_ShouldThrowException() {
    when(creatureRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        creatureService.getCreatureById(1L);
    });

    assertEquals("Creature not found", exception.getMessage());
}
```
**Leyenda:** *Verificación del manejo de errores cuando se intenta buscar una criatura que no existe en el sistema.*

### Caso: Actualización de Criatura
```java
@Test
void testUpdateCreature_Success() {
    Creature existingCreature = new Creature();
    existingCreature.setId(1L);
    existingCreature.setName("Fénix");

    Creature updatedCreature = new Creature();
    updatedCreature.setName("Fénix Renacido");
    updatedCreature.setDangerLevel(5);
    updatedCreature.setSize(10.0);

    when(creatureRepository.findById(1L)).thenReturn(Optional.of(existingCreature));
    when(creatureRepository.save(any(Creature.class))).thenReturn(existingCreature);

    Creature result = creatureService.updateCreature(1L, updatedCreature);

    assertEquals("Fénix Renacido", result.getName());
}
```
**Leyenda:** *Verifica que los datos de una criatura se actualicen de manera correcta en el servicio antes de invocar el guardado.*

### Caso: Eliminación Exitosa
```java
@Test
void testDeleteCreature_Success() {
    Creature creature = new Creature();
    creature.setId(1L);
    creature.setHealthStatus("healthy");

    when(creatureRepository.findById(1L)).thenReturn(Optional.of(creature));

    creatureService.deleteCreature(1L);

    verify(creatureRepository, times(1)).delete(creature);
}
```
**Leyenda:** *Prueba unitaria que confirma la llamada correcta a la función de eliminación del repositorio en una criatura sana.*

### Caso: Eliminación en Estado Crítico (Regla de Negocio)
```java
@Test
void testDeleteCreature_CriticalHealth_ShouldThrowException() {
    Creature creature = new Creature();
    creature.setId(1L);
    creature.setHealthStatus("critical");

    when(creatureRepository.findById(1L)).thenReturn(Optional.of(creature));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
        creatureService.deleteCreature(1L);
    });

    assertEquals("Cannot delete a creature in critical health", exception.getMessage());
}
```
**Leyenda:** *Comprobación de la regla de negocio que prohíbe explícitamente eliminar una criatura si su estado de salud es "critical".*

---

## 2. Pruebas Unitarias: ZoneService (Mockito y JUnit 5)

### Caso: Creación de Zona
```java
@Test
void testCreateZone_ShouldReturnSavedZone() {
    Zone zone = new Zone();
    zone.setName("Bosque Mágico");

    when(zoneRepository.save(any(Zone.class))).thenReturn(zone);

    Zone savedZone = zoneService.createZone(zone);

    assertNotNull(savedZone);
    assertEquals("Bosque Mágico", savedZone.getName());
}
```
**Leyenda:** *Validación de creación de una nueva zona dentro del servicio aislado.*

### Caso: Eliminación de Zona con Criaturas (Regla de Negocio)
```java
@Test
void testDeleteZone_WithCreatures_ShouldThrowException() {
    Zone zone = new Zone();
    zone.setId(1L);
    List<Creature> creatures = new ArrayList<>();
    creatures.add(new Creature());
    zone.setCreatures(creatures);

    when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));

    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
        zoneService.deleteZone(1L);
    });

    assertEquals("Cannot delete a zone that has creatures", exception.getMessage());
}
```
**Leyenda:** *Prueba de integridad lógica que impide la eliminación de una zona si ya tiene criaturas vinculadas a ella.*

---

## 3. Pruebas de Integración (Base de Datos H2)

### Caso: Inserción Real en la Base de Datos
```java
@Test
void testCreateCreature_ShouldPersistInDatabase() {
    Creature creature = new Creature();
    creature.setName("Unicornio");
    creature.setSize(15.0);
    creature.setDangerLevel(5);
    creature.setHealthStatus("healthy");

    creatureService.createCreature(creature);
    Optional<Creature> foundCreature = creatureRepository.findById(creature.getId());

    assertTrue(foundCreature.isPresent());
    assertEquals("Unicornio", foundCreature.get().getName());
}
```
**Leyenda:** *Prueba de integración: Se guarda una criatura en la base de datos en memoria (H2) y se consulta inmediatamente para verificar la persistencia exitosa.*

### Caso: Actualización Real en la Base de Datos
```java
@Test
void testUpdateCreature_ShouldPersistChangesInDatabase() {
    Creature creature = new Creature();
    creature.setName("Dragón");
    // ... setup ...
    Creature savedCreature = creatureService.createCreature(creature);

    Creature updatedCreature = new Creature();
    updatedCreature.setName("Dragón Anciano");
    // ... update ...
    
    creatureService.updateCreature(savedCreature.getId(), updatedCreature);
    
    Optional<Creature> foundCreature = creatureRepository.findById(savedCreature.getId());
    assertEquals("Dragón Anciano", foundCreature.get().getName());
}
```
**Leyenda:** *Prueba de integración: Modificación de un registro previamente guardado y validación de que los cambios se mantienen en la base de datos.*

### Caso: Eliminación Real en la Base de Datos
```java
@Test
void testDeleteCreature_ShouldRemoveFromDatabase() {
    Creature creature = new Creature();
    creature.setName("Troll");
    // ... setup ...
    Creature savedCreature = creatureService.createCreature(creature);

    creatureService.deleteCreature(savedCreature.getId());

    Optional<Creature> foundCreature = creatureRepository.findById(savedCreature.getId());
    assertFalse(foundCreature.isPresent());
}
```
**Leyenda:** *Prueba de integración: Eliminación de un registro persistido y comprobación de que al volver a buscarlo ya no existe en la base de datos.*
