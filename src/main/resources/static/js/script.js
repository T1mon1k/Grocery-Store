function updateRowSum(input) {
    const qty = parseInt(input.value) || 0;
    const row = input.closest('tr');
    const price = parseFloat(row.querySelector('td:nth-child(3) span').textContent) || 0;
    row.querySelector('.row-sum').textContent = (price * qty).toFixed(2);
}

window.onscroll = function () {
    const btn = document.getElementById("scrollTopBtn");
    if (document.body.scrollTop > 300 || document.documentElement.scrollTop > 300) {
        btn.style.display = "block";
    } else {
        btn.style.display = "none";
    }
};

function scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
}