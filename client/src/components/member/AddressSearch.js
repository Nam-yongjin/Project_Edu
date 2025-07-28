const AddressSearch = ({ onAddressSelected }) => {
    const openPostcode = () => {
        new window.daum.Postcode({
            oncomplete: function (data) {
                const address = data.address; // 사용자가 선택한 도로명 주소
                onAddressSelected(address);
            }
        }).open();
    };

    return (
        <div>
            <button className="border border-black px-1 bg-gray-300 active:bg-gray-400" onClick={openPostcode}>
                주소 찾기
            </button>
        </div>
    );
};
export default AddressSearch