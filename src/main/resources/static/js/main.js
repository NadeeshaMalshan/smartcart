// SmartCart Main JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Call all the initialization functions when the page is loaded
    initAnimations();
    initSmoothScrolling();
    initLoadingStates();
    initCartFunctionality();
    initSearch(); // <-- FIX: This was not being called
    initFormValidation(); // <-- FIX: This was not being called
    initMobileMenu();
});

// Animation initialization: Fades in elements on scroll
function initAnimations() {
    // This function is well-written and should work if your CSS has a .fade-in class.
    // Example CSS: .fade-in { animation: fadeIn 1s ease-in; }
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver(function(entries, observer) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.feature-card, .category-card').forEach(card => {
        observer.observe(card);
    });
}

// Smooth scrolling for anchor links (e.g., <a href="#contact">)
function initSmoothScrolling() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

// Adds a "Loading..." state to buttons on click
function initLoadingStates() {
    document.querySelectorAll('.btn').forEach(button => {
        button.addEventListener('click', function() {
            // NOTE: This loading state only applies to form submit buttons or links with "/api/" in them.
            // You can remove this 'if' condition if you want it to apply to all buttons.
            if (this.type === 'submit' || (this.href && this.href.includes('/api/'))) {
                if (this.classList.contains('btn-loading')) return; // Prevent multiple clicks

                this.classList.add('btn-loading');
                const originalText = this.innerHTML;
                this.innerHTML = '<span class="loading"></span> Loading...';

                // Reset the button after 3 seconds (as a fallback)
                setTimeout(() => {
                    this.classList.remove('btn-loading');
                    this.innerHTML = originalText;
                }, 3000);
            }
        });
    });
}

// Cart functionality
function initCartFunctionality() {
    // Add to cart button listeners
    // NOTE: Your buttons need the class "add-to-cart" and data attributes.
    // Example: <button class="add-to-cart" data-product-id="123" data-product-name="Fresh Apples">Add</button>
    document.querySelectorAll('.add-to-cart').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            addToCart(this);
        });
    });

    updateCartCount();
}

// Adds an item to the cart (simulated)
function addToCart(button) {
    const productId = button.dataset.productId;
    const productName = button.dataset.productName;

    if (!productName) {
        console.error('Button is missing "data-product-name" attribute.');
        return;
    }

    const originalText = button.innerHTML;
    button.innerHTML = '<span class="loading"></span> Adding...';
    button.disabled = true;

    // Simulate an API call
    setTimeout(() => {
        // Increment the cart count in localStorage
        let count = parseInt(localStorage.getItem('cartCount') || '0');
        localStorage.setItem('cartCount', count + 1);

        updateCartCount();
        showNotification(`${productName} added to cart!`, 'success');

        // Reset button state
        button.innerHTML = originalText;
        button.disabled = false;
    }, 1000);
}

// Updates the cart count in the navbar
function updateCartCount() {
    // NOTE: You need an element in your HTML for the count to display.
    // Example: <span class="cart-count">0</span>
    const cartCountElement = document.querySelector('.cart-count');
    if (cartCountElement) {
        let count = parseInt(localStorage.getItem('cartCount') || '0');
        cartCountElement.textContent = count;
        cartCountElement.style.display = count > 0 ? 'inline-block' : 'none';
    }
}

// Shows a temporary notification message
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 1050;';
    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    document.body.appendChild(notification);

    setTimeout(() => notification.remove(), 5000);
}

// Initializes the search functionality
function initSearch() {
    // NOTE: Your HTML needs an input with id="searchInput"
    const searchInput = document.querySelector('#searchInput');
    if (searchInput) {
        let searchTimeout;

        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const query = this.value.trim();

            if (query.length >= 2) {
                searchTimeout = setTimeout(() => performSearch(query), 300);
            }
        });
    }
}

// Performs a search (simulated)
function performSearch(query) {
    showNotification(`Searching for "${query}"...`, 'info');
    // In a real app, you would make an API call here.
}

// Initializes form validation
function initFormValidation() {
    // NOTE: This adds validation to ALL forms on the page.
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!validateForm(this)) {
                e.preventDefault();
                showNotification('Please fill out all required fields.', 'danger');
            }
        });
    });
}

// Validates required fields in a form
function validateForm(form) {
    let isValid = true;
    const requiredFields = form.querySelectorAll('[required]');

    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.classList.add('is-invalid');
            isValid = false;
        } else {
            field.classList.remove('is-invalid');
        }
    });

    return isValid;
}

// Mobile menu toggle functionality
function initMobileMenu() {
    // This is good for custom behavior, but note that Bootstrap 5's JS bundle
    // already handles the basic toggling of the menu.
    const navbarToggler = document.querySelector('.navbar-toggler');
    const navbarCollapse = document.querySelector('.navbar-collapse');

    if (navbarToggler && navbarCollapse) {
        // This part is helpful: it closes the mobile menu after a link is clicked.
        navbarCollapse.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', () => {
                if (navbarCollapse.classList.contains('show')) {
                    navbarToggler.click();
                }
            });
        });
    }
}


// Utility functions (defined but not used in this script)
const SmartCart = {
    formatCurrency: (amount) => new Intl.NumberFormat('en-LK', { style: 'currency', currency: 'LKR' }).format(amount),
    formatDate: (date) => new Intl.DateTimeFormat('en-GB').format(new Date(date)),
    debounce: (func, wait) => { /* ... implementation ... */ },
    throttle: (func, limit) => { /* ... implementation ... */ }
};

// Expose utility functions globally if needed by other scripts
window.SmartCart = SmartCart;