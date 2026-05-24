/* =============================================
   LUXEJEWEL - MAIN JAVASCRIPT
   ============================================= */

// ===== GET CSRF TOKEN =====
function getCsrfToken() {
  const meta = document.querySelector('meta[name="_csrf"]');
  const header = document.querySelector('meta[name="_csrf_header"]');
  if (meta && header) return { token: meta.content, header: header.content };
  // Try from hidden input
  const inp = document.querySelector('input[name="_csrf"]');
  if (inp) return { token: inp.value, header: 'X-CSRF-TOKEN' };
  return { token: '', header: 'X-CSRF-TOKEN' };
}

// ===== TOAST NOTIFICATION =====
function showToast(message, type = 'success') {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const icons = {
    success: 'fa-check-circle',
    danger:  'fa-exclamation-circle',
    warning: 'fa-exclamation-triangle',
    info:    'fa-info-circle'
  };
  const colors = {
    success: '#0f5132',
    danger:  '#842029',
    warning: '#664d03',
    info:    '#055160'
  };

  const id = 'toast_' + Date.now();
  const html = `
    <div id="${id}" class="toast toast-box show align-items-center mb-2"
         style="background:#fff;border-left:4px solid var(--${type === 'danger' ? 'bs-danger' : type === 'success' ? 'bs-success' : 'bs-warning'}, ${type === 'danger' ? '#dc3545' : type === 'success' ? '#198754' : '#ffc107'})"
         role="alert">
      <div class="d-flex align-items-center p-3 gap-2">
        <i class="fas ${icons[type] || icons.info} fa-lg" style="color:${colors[type] || colors.info}"></i>
        <div class="flex-grow-1" style="font-size:0.9rem;font-weight:500">${message}</div>
        <button type="button" class="btn-close btn-sm ms-2" onclick="removeToast('${id}')"></button>
      </div>
    </div>`;
  container.insertAdjacentHTML('beforeend', html);

  // Auto remove after 3.5s
  setTimeout(() => removeToast(id), 3500);
}

function removeToast(id) {
  const el = document.getElementById(id);
  if (el) { el.style.opacity = '0'; el.style.transform = 'translateX(20px)'; el.style.transition = '0.3s'; setTimeout(() => el.remove(), 300); }
}

// ===== ADD TO CART (AJAX) =====
function addToCart(productId, quantity = 1) {
  const csrf = getCsrfToken();
  fetch('/cart/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      [csrf.header]: csrf.token
    },
    body: `productId=${productId}&quantity=${quantity}`
  })
  .then(res => {
    if (res.status === 302 || res.redirected || (res.url && res.url.includes('/login'))) {
      // Not logged in - redirect to login
      window.location.href = '/login';
      return null;
    }
    return res.json();
  })
  .then(data => {
    if (!data) return;
    if (data.success) {
      showToast(data.message || 'Đã thêm vào giỏ hàng!', 'success');
      updateCartBadge(data.cartCount);
      // Animate cart icon
      const badge = document.getElementById('cartBadge');
      if (badge) {
        badge.style.transform = 'scale(1.5)';
        setTimeout(() => badge.style.transform = 'scale(1)', 300);
      }
    } else {
      showToast(data.message || 'Có lỗi xảy ra!', 'danger');
    }
  })
  .catch(err => {
    console.error(err);
    window.location.href = '/login';
  });
}

function updateCartBadge(count) {
  const badge = document.getElementById('cartBadge');
  if (badge) {
    badge.textContent = count;
    badge.style.display = count > 0 ? 'flex' : 'none';
  }
}

// ===== QUICK VIEW =====
function quickView(btn) {
  const productId = btn.dataset.id;
  const modal = new bootstrap.Modal(document.getElementById('quickViewModal'));
  const content = document.getElementById('quickViewContent');
  if (!content) return;

  content.innerHTML = '<div class="text-center py-5"><div class="spinner-border" style="color:var(--gold)"></div><p class="mt-2 text-muted">Đang tải...</p></div>';
  modal.show();

  fetch(`/shop/${productId}`)
    .then(res => {
      if (res.redirected || (res.url && res.url.includes('/login'))) {
        window.location.href = '/login';
        throw new Error('Not logged in');
      }
      return res.text();
    })
    .then(html => {
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, 'text/html');
      const name = doc.querySelector('.product-detail-name')?.textContent || '';
      const price = doc.querySelector('.current-price')?.textContent || '';
      const oldPrice = doc.querySelector('.old-price')?.textContent || '';
      const desc = doc.querySelector('.product-description p')?.textContent || '';
      const img = doc.querySelector('.main-product-image')?.src || '';
      const stock = doc.querySelector('.product-meta .badge')?.textContent || '';
      const brand = doc.querySelectorAll('.meta-value')[0]?.textContent || '';

      content.innerHTML = `
        <div class="row g-4">
          <div class="col-md-5">
            <img src="${img}" class="img-fluid rounded-3" style="object-fit:cover;max-height:280px;width:100%">
          </div>
          <div class="col-md-7">
            <h5 class="fw-bold mb-2" style="font-family:'Playfair Display',serif">${name}</h5>
            <div class="mb-3">
              ${oldPrice ? `<span class="text-muted text-decoration-line-through me-2">${oldPrice}</span>` : ''}
              <span style="color:var(--gold);font-size:1.4rem;font-weight:700">${price}</span>
            </div>
            ${brand ? `<p class="small text-muted mb-2">Thương hiệu: <strong>${brand}</strong></p>` : ''}
            ${stock ? `<span class="badge bg-success mb-3">${stock}</span>` : ''}
            <p class="text-muted small mb-4" style="display:-webkit-box;-webkit-line-clamp:3;-webkit-box-orient:vertical;overflow:hidden">${desc}</p>
            <div class="d-flex gap-3">
              <button class="btn btn-gold px-4 py-2 fw-bold" onclick="addToCart(${productId},1);bootstrap.Modal.getInstance(document.getElementById('quickViewModal')).hide()">
                <i class="fas fa-shopping-bag me-2"></i>Thêm Vào Giỏ
              </button>
              <a href="/shop/${productId}" class="btn btn-outline-secondary">Xem Chi Tiết</a>
            </div>
          </div>
        </div>`;
    })
    .catch(() => {
      content.innerHTML = '<div class="text-center py-4 text-muted"><i class="fas fa-exclamation-triangle fa-2x mb-2"></i><p>Không tải được thông tin sản phẩm</p></div>';
    });
}

// ===== LOAD CART COUNT on page load =====
document.addEventListener('DOMContentLoaded', function () {
  // Update cart count if logged in
  const badge = document.getElementById('cartBadge');
  if (badge) {
    fetch('/cart/count', { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
      .then(r => r.ok ? r.json() : null)
      .then(data => { if (data && data.count !== undefined) updateCartBadge(data.count); })
      .catch(() => {});
  }

  // Auto-dismiss alerts after 5s
  document.querySelectorAll('.alert.alert-dismissible').forEach(alert => {
    setTimeout(() => {
      const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
      if (bsAlert) bsAlert.close();
    }, 5000);
  });

  // Navbar scroll effect
  const nav = document.getElementById('mainNav');
  if (nav) {
    window.addEventListener('scroll', () => {
      nav.style.boxShadow = window.scrollY > 50
        ? '0 4px 20px rgba(0,0,0,0.4)'
        : '0 2px 20px rgba(0,0,0,0.3)';
    });
  }

  // Form validation styling
  document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', function(e) {
      if (!form.checkValidity()) {
        e.preventDefault();
        e.stopPropagation();
      }
      form.classList.add('was-validated');
    });
  });
});

// ===== TOGGLE PASSWORD VISIBILITY =====
function togglePassword(id, btn) {
  const input = document.getElementById(id);
  if (!input) return;
  const icon = btn.querySelector('i');
  if (input.type === 'password') {
    input.type = 'text';
    icon.classList.replace('fa-eye', 'fa-eye-slash');
  } else {
    input.type = 'password';
    icon.classList.replace('fa-eye-slash', 'fa-eye');
  }
}

// ===== SMOOTH SCROLL =====
document.querySelectorAll('a[href^="#"]').forEach(a => {
  a.addEventListener('click', function(e) {
    const id = this.getAttribute('href').slice(1);
    const el = document.getElementById(id);
    if (el) { e.preventDefault(); el.scrollIntoView({ behavior: 'smooth', block: 'start' }); }
  });
});

// ===== IMAGE LAZY LOAD FALLBACK =====
document.querySelectorAll('img').forEach(img => {
  img.addEventListener('error', function() {
    if (!this.src.includes('no-image.png')) {
      this.src = '/images/no-image.png';
    }
  });
});

// ===== CONFIRM DELETE HELPER =====
function confirmDelete(msg) {
  return confirm(msg || 'Bạn có chắc chắn muốn xóa?');
}
