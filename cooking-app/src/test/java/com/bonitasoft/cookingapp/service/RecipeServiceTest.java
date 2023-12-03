package com.bonitasoft.cookingapp.service;
import com.bonitasoft.cookingapp.entity.Recipe;
import com.bonitasoft.cookingapp.entity.User;
import com.bonitasoft.cookingapp.respository.RecipeRepository;
import com.bonitasoft.cookingapp.respository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    public void testAddRecipe() {
        Recipe mockRecipe = new Recipe();

        when(recipeRepository.save(mockRecipe)).thenReturn(mockRecipe);

        Recipe addedRecipe = recipeService.addRecipe(mockRecipe);

        verify(recipeRepository, times(1)).save(mockRecipe);
        assertEquals(mockRecipe, addedRecipe);
    }

    @Test
    public void testGetAllRecipes() {
        List<Recipe> mockRecipes = new ArrayList<>();

        when(recipeRepository.findAll()).thenReturn(mockRecipes);

        List<Recipe> recipes = recipeService.getAllRecipes();

        verify(recipeRepository, times(1)).findAll();
        assertEquals(mockRecipes, recipes);
    }


    @Test
    public void testGetRecipeById() {
        Long recipeId = 1L;
        Recipe mockRecipe = new Recipe();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));

        Recipe recipe = recipeService.getRecipeById(recipeId);

        verify(recipeRepository, times(1)).findById(recipeId);
        assertEquals(mockRecipe, recipe);
    }

    @Test
    public void testDeleteRecipe_RecipeNotFound() {
        Long recipeId = 1L;

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recipeService.deleteRecipe(recipeId));

        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, never()).delete(any());
    }

    @Test
    public void testDeleteRecipe_RecipeFound() {
        Long recipeId = 1L;
        Recipe mockRecipe = new Recipe();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockRecipe));

        recipeService.deleteRecipe(recipeId);

        verify(recipeRepository, times(1)).findById(recipeId);
        verify(recipeRepository, times(1)).delete(mockRecipe);
    }

    @Test
    public void testUpdateRecipe_RecipeAndAuthorFound() {
        Long recipeId = 1L;
        Long authorId = 2L;
        Recipe mockExistingRecipe = new Recipe();
        Recipe mockUpdatedRecipe = new Recipe();
        User mockAuthor = new User();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockExistingRecipe));

        when(userRepository.findById(authorId)).thenReturn(Optional.of(mockAuthor));

        when(recipeRepository.save(mockExistingRecipe)).thenReturn(mockExistingRecipe);

        Recipe updatedRecipe = recipeService.updateRecipe(recipeId, mockUpdatedRecipe);

        verify(recipeRepository, times(1)).findById(recipeId);
        verify(userRepository, times(1)).findById(authorId);
        verify(recipeRepository, times(1)).save(mockExistingRecipe);

        assertEquals(mockExistingRecipe, updatedRecipe);
    }

    @Test
    public void testUpdateRecipe_RecipeNotFound() {
        Long recipeId = 1L;
        Recipe mockUpdatedRecipe = new Recipe();
        Long authorId = 2L;

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recipeService.updateRecipe(recipeId, mockUpdatedRecipe));

        verify(recipeRepository, times(1)).findById(recipeId);
        verify(userRepository, never()).findById(any());
        verify(recipeRepository, never()).save(any());
    }

    @Test
    public void testUpdateRecipe_AuthorNotFound() {
        Long recipeId = 1L;
        Recipe mockExistingRecipe = new Recipe();
        Recipe mockUpdatedRecipe = new Recipe();
        Long authorId = 2L;

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(mockExistingRecipe));
        when(userRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recipeService.updateRecipe(recipeId, mockUpdatedRecipe));

        verify(recipeRepository, times(1)).findById(recipeId);
        verify(userRepository, times(1)).findById(authorId);
        verify(recipeRepository, never()).save(any());
    }

    @Test
    public void testGetRecipesByKeywords() {
        List<String> keywords = Arrays.asList("keyword1", "keyword2");
        Set<Recipe> mockRecipes = new HashSet<>();

        when(recipeRepository.getRecipesByKey(anyString())).thenReturn(mockRecipes);

        Set<Recipe> recipes = recipeService.getRecipesByKeywords(keywords);

        verify(recipeRepository, times(keywords.size())).getRecipesByKey(anyString());

    }

    @Test
    public void testGetRecipesByIngredients() {
        List<String> ingredients = List.of("ingredient1", "ingredient2");
        Collection mockRecipes = new ArrayList<>();

        when(recipeRepository.getRecipesByIngredients(anyString())).thenReturn(mockRecipes);

        Collection<? extends Recipe> recipes = recipeService.getRecipesByIngredients(ingredients);

        verify(recipeRepository, times(ingredients.size())).getRecipesByIngredients(anyString());

    }
    @Test
    public void testGetRecipesByAuthor() {
        User author = new User();
        List<Recipe> mockRecipes = new ArrayList<>();

        when(recipeRepository.getByAuthor(author)).thenReturn(mockRecipes);

        List<Recipe> recipes = recipeService.getRecipesByAuthor(author);

        verify(recipeRepository, times(1)).getByAuthor(author);

    }



}
