import React from "react";

const DemInfo = () => {
  const features = [
    {
      title: "기업 맞춤형 물품대여",
      desc: `실증에 적합한 다양한 물품을 기업이 등록하고 관리합니다. 
      교사는 필요에 따라 쉽게 신청하여 교육 현장에서 효율적으로 활용할 수 있습니다. 
      이를 통해 교육 현장에 최신 기술이 원활하게 도입될 수 있도록 돕습니다.`,
    },
    {
      title: "교육 현장 직접 활용",
      desc: `교사는 대여한 물품을 수업과 실습에 직접 활용하며, 
      학생들의 학습 효과 증진과 기술 이해도를 높입니다. 
      현장 경험을 바탕으로 기술 발전 방향에 대한 중요한 피드백도 제공됩니다.`,
    },
    {
      title: "상호 신뢰 기반 협력",
      desc: `기업과 교육자가 긴밀히 소통하여 혁신 기술을 교육에 자연스럽게 융합합니다. 
      협력 관계를 통해 교육의 질과 기술의 완성도를 함께 높여 나갑니다.`,
    },
  ];

  return (
    <div className="max-w-screen-xl mx-auto my-10">
      <div className="min-blank">
        <section className="w-full mx-auto my-16 text-gray-900">
          <div className="w-full border-b-2 border-indigo-600 pb-3 mb-10">
            <h1 className="newText-3xl font-bold text-gray-900 text-center tracking-wide mb-10">
              실증 지원 프로그램
            </h1>
          </div>

          <p className="newText-base text-center text-gray-700 mb-14 max-w-3xl mx-auto leading-relaxed">
            실증 지원 프로그램은 첨단 기술과 교육 현장을 효과적으로 연결하여
            교사들이 최신 물품을 대여해 직접 활용할 수 있도록 지원합니다.
            이를 통해 교육 현장에서 실제적인 기술 경험이 이루어지고,
            기업은 현장 피드백을 받아 기술을 더욱 발전시킬 수 있습니다.
          </p>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
            {features.map(({ title, desc }, i) => (
              <div
                key={i}
                className="bg-indigo-50 rounded-xl p-6 shadow-md border border-indigo-200 hover:shadow-lg transition-shadow duration-300"
              >
                <h3 className="newText-xl font-bold mb-4 text-indigo-700">{title}</h3>
                <p className="text-indigo-800 whitespace-pre-line">{desc}</p>
              </div>
            ))}
          </div>

          <p className="mt-16 text-center text-gray-600 newText-base max-w-3xl mx-auto leading-relaxed">
            이 프로그램은 교육과 기술의 접점을 넓히며,
            미래 인재 양성과 기술 혁신에 기여하는 것을 목표로 합니다.
            기업과 교육자가 협력하여 더 나은 학습 환경과 기술 발전을 함께 만들어가는 길에
            여러분의 적극적인 참여와 관심을 기다립니다.
            언제든지 문의해 주시면 상세히 안내해 드리겠습니다.
          </p>
        </section>
      </div>
    </div>
  );
};

export default DemInfo;