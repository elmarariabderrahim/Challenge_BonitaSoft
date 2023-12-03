package com.bonitasoft.cookingapp.controller;

import com.bonitasoft.cookingapp.entity.Recipe;
import com.bonitasoft.cookingapp.entity.User;
import com.bonitasoft.cookingapp.service.RecipeService;
import com.bonitasoft.cookingapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecipeControllerTest {
    @Mock
    private RecipeService recipeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RecipeController recipeController;

    @Test
    public void testGetAllRecipes() {

        List<Recipe> mockRecipes = new ArrayList<>();
        when(recipeService.getAllRecipes()).thenReturn(mockRecipes);


        ResponseEntity<List<Recipe>> responseEntity = recipeController.getAllRecipes();


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockRecipes, responseEntity.getBody());
    }

    @Test
    public void testAddRecipe_WithExistingAuthor() {
        Recipe mockRecipe = new Recipe();
        User mockAuthor = new User();

        mockRecipe.setAuthor(mockAuthor);
        when(userService.findUser(mockAuthor.getUsername())).thenReturn(mockAuthor);

        when(recipeService.addRecipe(mockRecipe)).thenReturn(mockRecipe);

        ResponseEntity<?> responseEntity = recipeController.addRecipe(mockRecipe);


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockRecipe, responseEntity.getBody());
    }

    @Test
    public void testAddRecipe_WithNonExistingAuthor() {
        Recipe mockRecipe = new Recipe();
        User nonExistingAuthor = new User();
        mockRecipe.setAuthor(nonExistingAuthor);
        when(userService.findUser(nonExistingAuthor.getUsername())).thenReturn(null);

        ResponseEntity<?> responseEntity = recipeController.addRecipe(mockRecipe);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals("Unable to create recipe, Author does not exist !", responseEntity.getBody());
    }

    @Test
    public void testGetRecipesByAuthor_AuthorFound() {
        String authorUsername = "existingAuthor";
        User mockAuthor = new User();

        when(userService.findUser(authorUsername)).thenReturn(mockAuthor);

        List<Recipe> mockRecipes = new ArrayList<>();
        when(recipeService.getRecipesByAuthor(mockAuthor)).thenReturn(mockRecipes);

        ResponseEntity<?> responseEntity = recipeController.getRecipesByAuthor(authorUsername);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockRecipes, responseEntity.getBody());
    }

    @Test
    public void testGetRecipesByAuthor_AuthorNotFound() {
        String authorUsername = "nonExistingAuthor";

        when(userService.findUser(authorUsername)).thenReturn(null);

        ResponseEntity<?> responseEntity = recipeController.getRecipesByAuthor(authorUsername);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("No Author found !", responseEntity.getBody());
    }

    @Test
    public void testGetRecipesByKeywords_WithEmptyKeywords() {
        List<String> emptyKeywords = new ArrayList<>();

        ResponseEntity<?> responseEntity = recipeController.getRecipesByKeywords(emptyKeywords);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("No given keywords !", responseEntity.getBody());
    }

    @Test
    public void testGetRecipesByKeywords_WithValidKeywords() {
        List<String> emptyKeywords = new ArrayList<>();
        emptyKeywords.add("keyword");

        Recipe recipe=new Recipe();
        recipe.setKeywords(emptyKeywords);

        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe);
        ResponseEntity<?> responseEntity = recipeController.getRecipesByKeywords(emptyKeywords);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetRecipesByIngredients_WithValidIngredients() {
        List<String> validIngredients = List.of("ingredient1", "ingredient2");

        Set<Recipe> mockRecipes = new HashSet<>();
        when(recipeService.getRecipesByIngredients(validIngredients)).thenReturn(mockRecipes);

        ResponseEntity<?> responseEntity = recipeController.getRecipesByIngredients(validIngredients);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockRecipes, responseEntity.getBody());
    }

    @Test
    public void testGetRecipesByIngredients_WithEmptyIngredients() {
        List<String> emptyIngredients = new ArrayList<>();

        ResponseEntity<?> responseEntity = recipeController.getRecipesByIngredients(emptyIngredients);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("No given keywords !", responseEntity.getBody());
    }

    @Test
    public void testUpdateRecipe() {
        Long recipeId = 1L;
        Long authorId = 2L;
        Recipe mockUpdatedRecipe = new Recipe();

        when(recipeService.updateRecipe(recipeId, mockUpdatedRecipe, authorId)).thenReturn(mockUpdatedRecipe);

        ResponseEntity<Recipe> responseEntity = recipeController.updateRecipe(recipeId, mockUpdatedRecipe, authorId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockUpdatedRecipe, responseEntity.getBody());
    }

    @Test
    public void testDeleteRecipe() {
        Long recipeId = 1L;

        ResponseEntity<Void> responseEntity = recipeController.deleteRecipe(recipeId);

        verify(recipeService, times(1)).deleteRecipe(recipeId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(null, responseEntity.getBody());
    }

    @Test
    public void testGetRecipeById() {
        Long recipeId = 1L;
        Recipe mockRecipe = new Recipe();

        when(recipeService.getRecipeById(recipeId)).thenReturn(mockRecipe);

        ResponseEntity<Recipe> responseEntity = recipeController.getRecipeById(recipeId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockRecipe, responseEntity.getBody());
    }

}
