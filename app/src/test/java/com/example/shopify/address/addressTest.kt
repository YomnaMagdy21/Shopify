package com.example.shopify.address

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.shopify.MainCoroutineRule
import com.example.shopify.MyAddress.viewModel.MyAddressViewModel
import com.example.shopify.getOrAwaitValue
import com.example.shopify.model.FakeShopifyRepository
import com.example.shopify.model.addressModel.AddNewAddress
import com.example.shopify.model.addressModel.Address
import com.example.shopify.model.addressModel.AddressesModel
import com.example.shopify.newAddress.viewModel.NewAddressViewModel
import com.example.shopify.utility.ApiState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AddressViewModelTest {
    @get:Rule
    val myRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var myAddressViewModel: MyAddressViewModel
    private lateinit var addNewAddressViewModel: NewAddressViewModel
    private lateinit var repo: FakeShopifyRepository
    private lateinit var addressesModel: AddressesModel
    private lateinit var newAddress: AddNewAddress

    @Before
    fun setUp() {
        repo = FakeShopifyRepository()
        myAddressViewModel = MyAddressViewModel(repo)
        addNewAddressViewModel = NewAddressViewModel(repo)
        addressesModel = AddressesModel(
            listOf(
                Address(address1 = "123 Main St", city = "New York", country = "United States", customer_id = 1L, default = true, first_name = "John", id = 1L, last_name = "Doe", name = "Home", phone = "123-456-7890", province = "NY", zip = "10001"),
                Address(address1 = "456 Elm St", city = "Los Angeles", country = "United States", customer_id = 1L, default = false, first_name = "Jane", id = 2L, last_name = "Smith", name = "Work", phone = "987-654-3210", province = "CA", zip = "90001")
            )
        )

        newAddress = AddNewAddress(
            address = Address(
                address1 = "456 Elm St",
                city = "Shelbyville",
                country = "USA"
            )
        )
    }

    @Test
    fun getAllAddresses_Success() = runBlockingTest {
        // When
        myAddressViewModel.getAllAddresses(1L)

        var result = myAddressViewModel.accessAllAddressesList.getOrAwaitValue()

        while (result is ApiState.Loading) {
            delay(100)
            result = myAddressViewModel.accessAllAddressesList.getOrAwaitValue()
        }

        // Then
        assertThat(result, `is`(instanceOf(ApiState.Success::class.java)))
        val successState = result as ApiState.Success<*>
        assertEquals(successState.data, addressesModel)
    }

    @Test
    fun deleteAddress_Success() = runBlockingTest {
        // Given
        val customerId = 1L
        val addressIdToRemove = 2L

        // When
        myAddressViewModel.deleteAddress(customerId, addressIdToRemove)

        // Then
        // Retrieve the updated addresses
        var result = myAddressViewModel.accessAllAddressesList.getOrAwaitValue()

        while (result is ApiState.Loading) {
            delay(100)
            result = myAddressViewModel.accessAllAddressesList.getOrAwaitValue()
        }

        // Assert that the result is success and contains the expected addresses model after deletion
        assertThat(result, `is`(instanceOf(ApiState.Success::class.java)))
        val successState = result as ApiState.Success<*>
        assertEquals(
            successState.data,
            AddressesModel(
                listOf(Address(address1 = "123 Main St", city = "New York", country = "United States", customer_id = 1L, default = true, first_name = "John", id = 1L, last_name = "Doe", name = "Home", phone = "123-456-7890", province = "NY", zip = "10001"))
            )
        )
    }

    @Test
    fun editAddress_Success() = runBlockingTest {
        // Given
        val customerId = 1L
        val addressId = 2L
        val updatedAddress = AddNewAddress(
            address = Address(
                address1 = "456 Elm St",
                city = "Springfield",
                country = "USA"
            )
        )

        // When
        myAddressViewModel.editAddress(customerId, addressId, updatedAddress)

        // Then
        // Retrieve the updated addresses
        var result = myAddressViewModel.accessAllAddressesList.getOrAwaitValue()

        while (result is ApiState.Loading) {
            delay(100)
            result = myAddressViewModel.accessAllAddressesList.getOrAwaitValue()
        }

        // Assert that the result is success and contains the expected addresses model after editing
        assertThat(result, `is`(instanceOf(ApiState.Success::class.java)))
        val successState = result as ApiState.Success<*>
        val updatedAddressesModel = successState.data as AddressesModel

        val editedAddress = updatedAddressesModel.addresses.find { it.id == addressId }
        assertNotNull(editedAddress)

        // Validate specific attributes of the edited address
        assertEquals(editedAddress?.address1, updatedAddress.address.address1)
        assertEquals(editedAddress?.city, updatedAddress.address.city)
        assertEquals(editedAddress?.country, updatedAddress.address.country)
        assertEquals(editedAddress?.customer_id, customerId)
    }

}


